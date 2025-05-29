package org.example;

import org.example.annotations.AfterSuite;
import org.example.annotations.BeforeSuite;
import org.example.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRunner {
    public static void runTests(Class<?> testClass) {

        //Указанный класс загружается в память
        System.out.println("Запускаем тесты для класса: " + testClass.getName());


        Method[] methods = testClass.getMethods();
        DataProcessorTests dataProcessorTests = new DataProcessorTests();
        int countBeforeSuite = 0;
        int countAfterSuite = 0;
        Method beforeSuite = null;
        Method afterSuite = null;
        List<Method> listTests = new ArrayList<>();

/*      Блок с проверками:
        Происходит проверка что методов с аннотациями @BeforeSuite не больше одного
        Происходит проверка что методов с аннотациями @AfterSuite не больше одного
        Проверка на статический метод
*/
        for (Method method : methods) {
            if (!method.getDeclaringClass().equals(testClass)) continue;

            if (method.isAnnotationPresent(BeforeSuite.class)) {
                beforeSuite = method;
                validStaticMethod(method, "BeforeSuite");
                countBeforeSuite++;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                afterSuite = method;
                validStaticMethod(method, "AfterSuite");
                countAfterSuite++;
            }
            if (method.isAnnotationPresent(Test.class)) {
                listTests.add(method);
            }
        }

        checkSingleAnnotationCount(countBeforeSuite, "BeforeSuite");
        checkSingleAnnotationCount(countAfterSuite, "AfterSuite");

        //Выполняется метод с аннотацией @BeforeSuite, если такой есть
        if (beforeSuite != null) {
            try {
                beforeSuite.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        //Выполняются методы с аннотациями @Test в соответствии с их приоритетом. Вначале выполняются те методы, у которых приоритет выше.
        if (!listTests.isEmpty()){
            for (Method method : listTests) {
                try {
                    method.invoke(dataProcessorTests);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //Выполняется метод с аннотацией @AfterSuite, если такой ест
        if (afterSuite != null) {
            try {
                afterSuite.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void checkSingleAnnotationCount(int count, String annotationName) {
        System.out.println("Кол-во аннотации @" + annotationName + ": " + count);
        if (count > 1) {
            throw new RuntimeException("Ошибка: больше одного метода с аннотацией @" + annotationName + "!");
        }
    }

    private static void validStaticMethod (Method method, String annotationName){
        if (!Modifier.isStatic(method.getModifiers())){
            throw new RuntimeException("Метод с аннотацией @" + annotationName + " должен быть static!");
        }
    }

}
