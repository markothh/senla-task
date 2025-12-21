package Task3;

public class Consumer extends Thread {
    private final Buffer buffer;
    private final int processingInterval;

    public Consumer(Buffer buffer, int processingInterval) {
        this.buffer = buffer;
        this.processingInterval = processingInterval;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (buffer) {
                while (buffer.isEmpty()) {
                    try {
                        System.out.println("Буфер пуст!\n");
                        buffer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                consume(buffer.get());
                buffer.notify();
            }

            try {
                Thread.sleep(processingInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void consume(int value) {
        System.out.printf("Обработка значения: %d%n", value);
    }
}
