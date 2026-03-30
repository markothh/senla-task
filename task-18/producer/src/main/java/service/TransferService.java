package service;

import dto.TransferDTO;
import entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    private static final String TOPIC = "transfers";

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransferDTO> kafkaTemplate;

    private final Map<Long, Account> accountsMap = new HashMap<>();

    public TransferService(AccountRepository accountRepository,
                           KafkaTemplate<String, TransferDTO> kafkaTemplate) {
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void initAccounts() {
        logger.info("Начинается инициализация счетов");

        if (accountRepository.count() == 0) {
            for (int i = 0; i < 1000; i++) {
                Account acc = new Account();
                acc.setBalance(BigDecimal.valueOf(10000));
                accountRepository.save(acc);
            }
            logger.info("Таблица со счетами была пустой. Создано 1000 новых счетов");
        }

        List<Long> ids = accountRepository.findAllIds();
        for (Long id : ids) {
            accountsMap.put(id, null);
        }
        logger.info("Загружено {} счетов", accountsMap.size());
    }

    @Scheduled(fixedDelay = 200)
    public void send() {
        if (accountsMap.isEmpty()) {
            return;
        }

        Long fromId = randomAccountId();
        Long toId = randomAccountId();

        while (toId.equals(fromId)) {
            toId = randomAccountId();
        }

        BigDecimal amount = BigDecimal.valueOf(
                ThreadLocalRandom.current().nextDouble(1, 1000)
        ).setScale(2, RoundingMode.HALF_UP);

        TransferDTO dto = new TransferDTO();
        dto.setFromId(fromId);
        dto.setToId(toId);
        dto.setAmount(amount);

        try {
            kafkaTemplate.executeInTransaction(operations ->
                    operations.send(TOPIC, fromId.toString(), dto)
            );
            logger.info("Отправлен перевод: с={}, на={}, сумма={}", fromId, toId, amount);
        } catch (Exception e) {
            logger.error("Не удалось отправить перевод: ошибка={}", e.getMessage());
        }
    }

    private Long randomAccountId() {
        Long[] ids = accountsMap.keySet().toArray(new Long[0]);
        return ids[ThreadLocalRandom.current().nextInt(ids.length)];
    }
}
