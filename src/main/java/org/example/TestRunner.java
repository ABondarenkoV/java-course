package org.example;

import org.example.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
        Method beforeTest = null;
        Method afterTest = null;
        Method csvSource = null;
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
            if (method.isAnnotationPresent(AfterTest.class)) {
                afterTest = method;
            }
            if (method.isAnnotationPresent(BeforeTest.class)) {
                beforeTest = method;
            }
            if (method.isAnnotationPresent(CsvSource.class)) {
                csvSource = method;

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

            listTests.sort(Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority()));


            for (Method method : listTests) {
                try {
                    //System.out.println("Выполняется тест: " + method.getName() + ", приоритет: " + method.getAnnotation(Test.class).priority());
                    //Можете добавить аннотации @BeforeTest и @AfterTest, методы с такими аннотациями должны выполняться перед каждым и после каждого теста соответственно.
                    if (beforeTest != null) {
                        beforeTest.invoke(null);
                    }
                    
                    method.invoke(dataProcessorTests);

                    if (afterTest != null) {
                        afterTest.invoke(null);
                    }
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

        if (csvSource != null) {

            //System.out.println(csvSource);

/*            List<String> annotationArgList = Arrays.stream(csvSource.getAnnotation(CsvSource.class).value().split(","))
                    .map(String::trim)
                    .toList();*/

            //System.out.println("value: " + csvSource.getAnnotation(CsvSource.class).value());
            //System.out.println("Типы: " +Arrays.toString(csvSource.getParameterTypes()));

            //Class<?>[] paramTypes = csvSource.getParameterTypes();
            List<Class<?>> paramTypeList = Arrays.asList(csvSource.getParameterTypes());
            List<String> annotationArgList = Arrays.stream(csvSource.getAnnotation(CsvSource.class).value().split(","))
                    .map(String::trim)
                    .toList();

            Object[] convertedArgs = new Object[annotationArgList.size()];

//            annotationArgList.forEach(System.out::println);
//            paramTypeList.forEach(System.out::println);
//
//            System.out.println(annotationArgList.size());

            for (int i = 0; i < annotationArgList.size(); i++) {
                if (annotationArgList.size() == paramTypeList.size()) {//Но должно ли совпадать ?
                    convertedArgs[i] = typeDefinition(annotationArgList.get(i), paramTypeList.get(i));
                }else
                    throw new ArrayIndexOutOfBoundsException("Не совпадает кол-во аргументов");
            }

/*            Arrays.stream(convertedArgs).forEach(convertedArg -> {
                System.out.println(convertedArg.getClass().getSimpleName());
            });*/

            try {
                csvSource.invoke(CsvSource.class,convertedArgs);
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

    private static Object typeDefinition (String value, Class<?> type){
/*        for (String elm : stringList){
            if(elm.matches("-?\\d+")){
                result.add(Integer.parseInt(elm));
                //System.out.println("int");
            } else if (elm.matches("[\\p{IsAlphabetic}\\s]+")) {
                result.add(elm);
                //System.out.println("String");
            }
        }*/
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }  else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == String.class) {
            return value;
        }

        throw new RuntimeException("Данный тип не определен : " + type.getName());
    }


}
