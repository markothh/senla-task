import config.KafkaProducerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ProducerApp {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(KafkaProducerConfig.class);
    }
}
