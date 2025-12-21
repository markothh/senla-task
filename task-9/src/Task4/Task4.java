package Task4;

public class Task4 {
    public static void main(String[] args) throws InterruptedException {
        Thread timePrinter = new TimePrinter(2);
        timePrinter.start();

        timePrinter.join();
    }
}
