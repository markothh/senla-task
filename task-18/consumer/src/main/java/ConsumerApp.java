import config.KafkaConsumerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConsumerApp {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(KafkaConsumerConfig.class);
    }
}
