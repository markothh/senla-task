package Task3;

import java.util.Random;

public class Producer extends Thread {
    private final Buffer buffer;
    private final int processingInterval;
    private final Random random = new Random();

    public Producer(Buffer buffer, int processingInterval) {
        this.buffer = buffer;
        this.processingInterval = processingInterval;

    }

    @Override
    public void run() {
        while (true) {
            synchronized (buffer) {
                while (buffer.isFull()) {
                    try {
                        System.out.println("Буфер полон!\n");
                        buffer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                buffer.put(produce());
                buffer.notify();
            }

            try {
                Thread.sleep(processingInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int produce() {
        int value = random.nextInt(100);
        System.out.printf("Произведено значение: %d%n", value);
        return value;
    }
}
