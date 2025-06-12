package org.example.task3;

import java.util.LinkedList;
import java.util.concurrent.Executors;

public class ThreadPool {
    // Внутри пула очередь задач на исполнение организуется через LinkedList<Runnable>.
    private final LinkedList<Runnable> taskQueue = new LinkedList<>();
    private final Object monitor = new Object();

    public ThreadPool(int capacity) {
        //В качестве аргументов конструктора пулу передается его емкость (количество рабочих потоков).
        //Как только пул создан, он сразу инициализирует и запускает потоки.
        for (int i = 0; i < capacity; i++) {
            Thread thread = new Thread(new Worker());
            thread.start();
            System.out.println("Поток создан: " + thread.getName());
        }
    }

    public void execute(Runnable task) {
        //При выполнении у пула потоков метода execute(Runnabler), указанная задача должна попасть в очередь исполнения,
        if (task == null)
            throw new NullPointerException();
        synchronized (monitor) {
            taskQueue.add(task);
            System.out.println("Задача добавлена в очередь: " + taskQueue.size());
            monitor.notifyAll();
        }

    }

    public void shutdown(){}
    public void awaitTermination(){}

    private class Worker implements Runnable {
        @Override
        public void run() {
            System.out.println("Старт потока: " + Thread.currentThread().getName());

            Runnable task;
            //и как только появится свободный поток – должна быть выполнена.
            while (true) {
                synchronized (monitor) {
                    while (taskQueue.isEmpty()) {
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
