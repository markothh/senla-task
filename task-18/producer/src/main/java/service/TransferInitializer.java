package service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class TransferInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TransferService transferService;

    public TransferInitializer(TransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        transferService.initAccounts();
    }
}
