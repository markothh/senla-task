package service;

import dto.TransferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferListener {
    private static final Logger logger = LoggerFactory.getLogger(TransferListener.class);

    private final TransferService service;

    public TransferListener(TransferService service) {
        this.service = service;
    }

    @KafkaListener(topics = "transfers", containerFactory = "batchFactory")
    public void listen(List<TransferDTO> messages, Acknowledgment ack) {
        for (TransferDTO dto : messages) {
            service.process(dto);
        }
        ack.acknowledge();
    }
}
