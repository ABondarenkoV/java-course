package org.example;

import org.example.annotations.*;

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
        System.out.println("- Загрузка данных ...");
    }

    @Test(priority = 4)
    public void validateData() {
        System.out.println("- Валидация данных ...");
    }

    @Test(priority = 2)
    public void process() {
        System.out.println("- Обработка данных ...");
    }

    @Test(priority = 7)
    public void exportData() {
        System.out.println("- Экспорт данных ...");
    }

    @Test(priority = 10)
    public void generateReport() {
        System.out.println("- Генерация отчётов ...");
    }

    @AfterSuite
    public static void closingConnect() {
        System.out.println("Закрытие коннектов ...");
    }

    @BeforeTest
    public static void reconnection() {
        System.out.println("Before_Переподключение");
    }

    @AfterTest
    public static void loading() {
        System.out.println("After_Загрузка");
    }

    @CsvSource("100, Java, 20, true, 33.4")
    public static void testMethod(int a, String b, int c, boolean d, float e) {
        System.out.println("Метод с переданными параметрами : " + a + ", " + b + ", " + c + ", " + d + ", " + e );
    }
}
