import controller.MenuController;
import model.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();

        MenuController menuController = context.getBean(MenuController.class);
        menuController.run();
    }
}
