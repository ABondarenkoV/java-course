package org.example.task3;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

public class ThreadPool {
    // Внутри пула очередь задач на исполнение организуется через LinkedList<Runnable>.
    private final LinkedList<Runnable> taskQueue = new LinkedList<>();
    private final Object monitor = new Object();
    private volatile boolean isShutdown = false;
    private final List<Thread> workerThreads = new LinkedList<>();

    public ThreadPool(int capacity) {
        //В качестве аргументов конструктора пулу передается его емкость (количество рабочих потоков).
        //Как только пул создан, он сразу инициализирует и запускает потоки.
        for (int i = 0; i < capacity; i++) {
            Thread thread = new Thread(new Worker());
            workerThreads.add(thread); // учет потока
            thread.start();
            System.out.println("Поток создан: " + thread.getName());
        }
    }

    public void execute(Runnable task) {
        //При выполнении у пула потоков метода execute(Runnabler), указанная задача должна попасть в очередь исполнения,
        if (task == null)
            throw new NullPointerException();
        synchronized (monitor) {
            if(isShutdown){
                throw new IllegalStateException("Пул закрыт - для задач");
            }

            taskQueue.add(task);
            System.out.println("Задача добавлена в очередь: " + taskQueue.size());
            monitor.notifyAll();
        }

    }
    //Также необходимо реализовать метод shutdown(), после выполнения которого новые задачи больше не принимаются пулом
    //(при попытке добавить задачу можно бросать IllegalStateException), и все потоки для которых больше нет задач завершают свою работу.
    public void shutdown(){
        synchronized (monitor) {
            isShutdown = true;
            monitor.notifyAll();
        }
    }
    //Дополнительно можно добавить метод awaitTermination() без таймаута, работающий аналогично стандартным пулам потоков
    public void awaitTermination(){
        for (Thread thread : workerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Все рабочие потоки завершены");
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            System.out.println("Старт потока: " + Thread.currentThread().getName());

            Runnable task;
            //и как только появится свободный поток – должна быть выполнена.
            while (true) {
                synchronized (monitor) {
                    //Если нет очереди из задач и есть флаг, то работа завершена потока
                    while (taskQueue.isEmpty()) {
                        if (isShutdown) {
                            System.out.println("Поток завершает работу: " + Thread.currentThread().getName() + " и больше не принимает задач");
                            return;
                        }
                        try {
                            System.out.println("Ожидание задачи: " + Thread.currentThread().getName());
                            monitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    task = taskQueue.removeFirst();
                    System.out.println("Задача получена: " + Thread.currentThread().getName());
                }
                task.run();
                System.out.println("Выполнение задачи...");
            }
        }
    }
}
