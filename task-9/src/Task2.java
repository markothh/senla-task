public class Task2 {
    private static boolean turn;
    private final static Object obj = new Object();
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                synchronized (obj) {
                    while (turn) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    System.out.println("Thread 1");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    turn = true;
                    obj.notify();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                synchronized (obj) {
                    while (!turn) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    System.out.println("Thread 2");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    turn = false;
                    obj.notify();
                }
            }
        });

        t1.start();
        t2.start();
    }
}
