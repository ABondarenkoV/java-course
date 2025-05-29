package org.example;

import org.example.annotations.AfterSuite;
import org.example.annotations.BeforeSuite;
import org.example.annotations.Test;

public class DataProcessorTests {

    @BeforeSuite
    public static void initialize() {
        System.out.println("Инициализация системы ...");
    }

/*    @BeforeSuite
    public static void initializeDouble() {
        System.out.println("Инициализация системы 2 ...");
    }*/

    @Test(priority = 1)
    public void loadData() {
        System.out.println("Загрузка данных ...");
    }

    @Test(priority = 4)
    public void validateData() {
        System.out.println("Валидация данных ...");
    }

    @Test(priority = 2)
    public void process() {
        System.out.println("Обработка данных ...");
    }

    @Test(priority = 7)
    public void exportData() {
        System.out.println("Экспорт данных ...");
    }

    @Test(priority = 10)
    public void generateReport() {
        System.out.println("Генерация отчётов ...");
    }

    @AfterSuite
    public static void closingConnect() {
        System.out.println("Закрытие коннектов ...");
    }
}
