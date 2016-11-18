package ru.parserprices.myparser;

/**
 * Created by vnc on 11/16/16.
 */
public class MyThreads extends Thread {

    public MyThreads(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println("Стартуем наш поток " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
            // для примера будем выполнять обработку базы данных
            doDBProcessing();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Заканчиваем наш поток " + Thread.currentThread().getName());
    }
    // метод псевдообработки базы данных
    private void doDBProcessing() throws InterruptedException {
        Thread.sleep(5000);
    }
}
