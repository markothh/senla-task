package Task4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimePrinter extends Thread {
    private final int interval;

    public TimePrinter(int interval) {
        this.interval = interval;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            try {
                Thread.sleep(interval* 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
