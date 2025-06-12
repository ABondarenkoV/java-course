package org.example;

import org.example.task1.DataProcessorTests;
import org.example.task1.TestRunner;
import org.example.task2.CollectionsBoard;
import org.example.task3.ThreadPool;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        //Task1
        //TestRunner.runTests(DataProcessorTests.class);

        //Task2
        //CollectionsBoard.collectionsRun();

        //Task3
        ThreadPool threadPool = new ThreadPool(3);

        for (int i = 0; i < 5; i++) {
            int taskId = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("Начало задачи " + taskId + " в потоке " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        }


/*        ThreadPool pool = new ThreadPool(2);

        pool.execute(() -> {
            System.out.println("Задача 1 выполняется в " + Thread.currentThread().getName());
        });

        pool.execute(() -> {
            System.out.println("Задача 2 выполняется в " + Thread.currentThread().getName());
        });

        pool.execute(() -> {
            System.out.println("Задача 3 выполняется в " + Thread.currentThread().getName());
        });*/


/*
        Thread thread = new Thread(() ->{
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Main thread started");
*/


/*        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
           executorService.execute(() -> {
               try {
                   Thread.sleep(1000);
                   System.out.println(Thread.currentThread().getName());
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }

           });
        }
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();*/




    }

}