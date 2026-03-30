package service;

import dto.TransferDTO;
import entity.Account;
import entity.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import repository.TransferRepository;

@Component
public class TransferProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TransferProcessor.class);

    private static final String STATUS_SUCCESS = "готово";
    private static final String STATUS_ERROR = "завершилось с ошибкой";

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferProcessor(AccountRepository accountRepository,
                             TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public void processTransfer(TransferDTO dto) {
        logger.info("Начата обработка перевода: с={}, на={}, сумма={}",
                dto.getFromId(), dto.getToId(), dto.getAmount());

        Account from = accountRepository.findById(dto.getFromId())
                .orElseThrow(() -> new RuntimeException(
                        "Счет списания не найден: id=" + dto.getFromId()));

        Account to = accountRepository.findById(dto.getToId())
                .orElseThrow(() -> new RuntimeException(
                        "Счет зачисления не найден: id=" + dto.getToId()));

        if (from.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException(String.format(
                    "Недостаточно средств: на счету %s %s, требуется %s",
                    dto.getFromId(), from.getBalance(), dto.getAmount()));
        }

        from.setBalance(from.getBalance().subtract(dto.getAmount()));
        to.setBalance(to.getBalance().add(dto.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);

        Transfer transfer = new Transfer();
        transfer.setFromAccountId(dto.getFromId());
        transfer.setToAccountId(dto.getToId());
        transfer.setAmount(dto.getAmount());
        transfer.setStatus(STATUS_SUCCESS);

        transferRepository.save(transfer);

        logger.info("Перевод успешно завершен");
    }

    @Transactional
    public void saveFailedTransfer(TransferDTO dto, String errorMessage) {
        logger.error("Перевод завершился с ошибкой: причина={}", errorMessage);

        Transfer transfer = new Transfer();
        transfer.setFromAccountId(dto.getFromId());
        transfer.setToAccountId(dto.getToId());
        transfer.setAmount(dto.getAmount());
        transfer.setStatus(STATUS_ERROR);

        transferRepository.save(transfer);
    }
}
