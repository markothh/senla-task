package service;

import dto.TransferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final TransferProcessor transferProcessor;

    public TransferService(TransferProcessor transferProcessor) {
        this.transferProcessor = transferProcessor;
    }

    public void process(TransferDTO dto) {
        try {
            transferProcessor.processTransfer(dto);
        } catch (Exception e) {
            logger.error("Ошибка валидации перевода: {}", e.getMessage());
            try {
                transferProcessor.saveFailedTransfer(dto, e.getMessage());
            } catch (Exception ex) {
                logger.error("Не удалось сохранить запись об ошибке");
            }
        }
    }
}
