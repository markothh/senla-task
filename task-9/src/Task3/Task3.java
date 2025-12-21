package Task3;

public class Task3 {
    private static final double processingRate = 0.75; //отношение "скорости работы" потребителя к производителю;
                                                       // для тестирования поведения при заполненном/пустом буфере
    private static final int processingInterval = 100;
    public static void main(String[] args) {
        Buffer buffer = new Buffer(3);
        Producer producer = new Producer(buffer, (int)(processingInterval*processingRate));
        Consumer consumer = new Consumer(buffer, processingInterval);

        producer.start();
        consumer.start();
    }
}
