public class Task1 {
    private static final Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        //NEW
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                try {
                    Thread.sleep(500);
                    obj.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.println(t1.getState());

        Thread t2 = new Thread(() -> {
            synchronized (obj) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t2.start();

        //RUNNABLE
        t1.start();
        System.out.println(t1.getState());
        Thread.sleep(200);

        //BLOCKED
        System.out.println(t1.getState());

        //TIMED_WAITING
        Thread.sleep(200);
        System.out.println(t1.getState());

        //WAITING
        Thread.sleep(500);
        System.out.println(t1.getState());

        //TERMINATED
        synchronized (obj) {
            obj.notify();
        }

        t1.join();
        System.out.println(t1.getState());

    }
}
