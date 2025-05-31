package org.example;

import org.example.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {
    public static void runTests(Class<?> testClass) {

        //Указанный класс загружается в память
        System.out.println("Запускаем тесты для класса: " + testClass.getName());

        Method[] methods = testClass.getMethods();
        Object dataProcessorTests;
        Constructor<?> constructor;
        try {
            constructor = testClass.getConstructor();
            dataProcessorTests = constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        int countBeforeSuite = 0;
        int countAfterSuite = 0;
        List<Method> listTests = new ArrayList<>();
        List<Method> arrBeforeSuite = new ArrayList<>();
        List<Method> arrAfterSuite = new ArrayList<>();
        List<Method> arrBeforeTest = new ArrayList<>();
        List<Method> arrAfterTest = new ArrayList<>();
        List<Method> arrCsvSource = new ArrayList<>();


/*      Блок с проверками:
        Происходит проверка что методов с аннотациями @BeforeSuite не больше одного
        Происходит проверка что методов с аннотациями @AfterSuite не больше одного
        Проверка на статический метод
*/
        for (Method method : methods) {
            if (!method.getDeclaringClass().equals(testClass)) continue;

            if (method.isAnnotationPresent(BeforeSuite.class)) {
                arrBeforeSuite.add(method);
                countBeforeSuite++;
                if (countBeforeSuite > 1) {
                    throw new RuntimeException("Ошибка: больше одного метода с аннотацией @BeforeSuite!");
                }
                validStaticMethod(method, "BeforeSuite");
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                arrAfterSuite.add(method);
                countAfterSuite++;
                if (countAfterSuite > 1) {
                    throw new RuntimeException("Ошибка: больше одного метода с аннотацией @AfterSuite!");
                }
                validStaticMethod(method, "AfterSuite");
            }
            if (method.isAnnotationPresent(AfterTest.class)) {
                arrAfterTest.add(method);
            }
            if (method.isAnnotationPresent(BeforeTest.class)) {
                arrBeforeTest.add(method);
            }
            if (method.isAnnotationPresent(CsvSource.class)) {
                arrCsvSource.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                if (method.getAnnotation(Test.class).priority() < 1 || method.getAnnotation(Test.class).priority() > 10){
                    throw new RuntimeException("Ошибка: у метода " + method.getName() + " int в пределах от 1 до 10 включительно");
                }
                listTests.add(method);
            }
        }

        //Выполняется метод с аннотацией @BeforeSuite, если такой есть
        for (Method elBeforeSuite : arrBeforeSuite) {
            try {
                assert elBeforeSuite != null;
                elBeforeSuite.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        //Выполняются методы с аннотациями @Test в соответствии с их приоритетом. Вначале выполняются те методы, у которых приоритет выше.
        if (!listTests.isEmpty()) {

            listTests.sort(Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority()));

            for (Method method : listTests) {
                try {
                    //System.out.println("Выполняется тест: " + method.getName() + ", приоритет: " + method.getAnnotation(Test.class).priority());
                    //Можете добавить аннотации @BeforeTest и @AfterTest, методы с такими аннотациями должны выполняться перед каждым и после каждого теста соответственно.
                    for (Method elBeforeTest : arrBeforeTest) {
                        elBeforeTest.invoke(null);
                    }

                    method.invoke(dataProcessorTests);

                    for (Method elAfterTest : arrAfterTest) {
                        elAfterTest.invoke(null);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //Выполняется метод с аннотацией @AfterSuite, если такой ест

        for (Method elAfterSuite : arrAfterSuite) {
            try {
                assert elAfterSuite != null;
                elAfterSuite.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        //Добавьте аннотацию @CsvSource ...

        for (Method elCsvSource : arrCsvSource) {
            assert elCsvSource != null;
            List<Class<?>> paramTypeList = Arrays.asList(elCsvSource.getParameterTypes());
            List<String> annotationArgList = Arrays.stream(elCsvSource.getAnnotation(CsvSource.class).value().split(",")).map(String::trim).toList();

            Object[] convertedArgs = new Object[annotationArgList.size()];

            for (int i = 0; i < annotationArgList.size(); i++) {
                if (annotationArgList.size() == paramTypeList.size()) {
                    convertedArgs[i] = typeDefinition(annotationArgList.get(i), paramTypeList.get(i));
                } else throw new ArrayIndexOutOfBoundsException("Не совпадает кол-во аргументов");
            }

            try {
                elCsvSource.invoke(CsvSource.class, convertedArgs);
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

    private static void validStaticMethod(Method method, String annotationName) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("Метод с аннотацией @" + annotationName + " должен быть static!");
        }
    }

    private static Object typeDefinition(String value, Class<?> type) {

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == String.class) {
            return value;
        }

        throw new RuntimeException("Данный тип не определен : " + type.getName());
    }
}
