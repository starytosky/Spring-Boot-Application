package test;

public class ThreadTest {
    public static void main(String[] args) {
        System.out.println("main start ..");
        Thread t = new Thread(() -> {
            System.out.println("thread run");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("thread end");
        });
        t.start();
        System.out.println("main end");
    }
}
