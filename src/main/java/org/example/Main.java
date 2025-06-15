package org.example;

import jdk.jfr.StackTrace;
import org.example.task1.DataProcessorTests;
import org.example.task1.TestRunner;
import org.example.task2.CollectionsBoard;
import org.example.task3.ThreadPool;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        //Task1
        //TestRunner.runTests(DataProcessorTests.class);

        //Task2
        //CollectionsBoard.collectionsRun();

        //Task3
        ThreadPool threadPool = new ThreadPool(2);

        for (int i = 1; i < 10; i++) {
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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        threadPool.shutdown();
        threadPool.awaitTermination();
        System.out.println("Главный поток  = Финал - " + Thread.currentThread().getName());

        try {
            threadPool.execute(() -> {
                System.out.println("Эта задача не должна быть выполнена: " + Thread.currentThread().getName());
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
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

    }

}