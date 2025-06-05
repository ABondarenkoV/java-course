package org.example.task2;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionsBoard {
    public static void collectionsRun() {

        //1. Найдите в списке целых чисел 3-е наибольшее число (пример: 5 2 10 9 4 3 10 1 13 => 10)

        Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                .sorted(Comparator.reverseOrder()).limit(3).skip(2)
                .forEach(System.out::println);

        System.out.println("||");

        Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13)
                .sorted(Comparator.reverseOrder())
                .skip(2).findFirst().ifPresent(System.out::println);

        System.out.println("--------------");

        //2. Найдите в списке целых чисел 3-е наибольшее «уникальное» число (пример: 5 2 10 9 4 3 10 1 13 => 9,
        //в отличие от прошлой задачи здесь разные 10 считает за одно число)
        Stream.of(5, 2, 10, 9, 4, 3, 10, 1, 13).distinct()
                .sorted(Comparator.reverseOrder()).limit(3).skip(2)
                .forEach(System.out::println);

        System.out.println("--------------");

        //3. Имеется список объектов типа Сотрудник (имя, возраст, должность), необходимо получить список имен 3
        //самых старших сотрудников с должностью «Инженер», в порядке убывания возраста
        List<Employee> listEmpl = new ArrayList<>(Arrays.asList(
                new Employee("Андрей",25, Employee.Position.MANAGER),
                new Employee("Юлия",29, Employee.Position.MANAGER),
                new Employee("Антон",45, Employee.Position.DIRECTOR),
                new Employee("Елезавета",45, Employee.Position.ENGINEER),
                new Employee("Иван",27, Employee.Position.ENGINEER),
                new Employee("Сергей",36, Employee.Position.ENGINEER),
                new Employee("Эльдар",22, Employee.Position.ENGINEER)
                ));

        listEmpl.stream().filter(employee->employee.getPosition() == Employee.Position.ENGINEER)
                .sorted(Comparator.comparing(Employee::getAge).reversed()).limit(3)
                .map(Employee::getName)
                .forEach(System.out::println);

        System.out.println("--------------");

        //4. Имеется список объектов типа Сотрудник (имя, возраст, должность), посчитайте средний возраст сотрудников с должностью «Инженер»
        listEmpl.stream()
                .filter(e -> e.getPosition() == Employee.Position.ENGINEER)
                .mapToInt(Employee::getAge)
                .average()
                .ifPresent(avg -> System.out.println("AVG age: " + avg));

        System.out.println("--------------");

        //5. Найдите в списке слов самое длинное
        Stream.of("Желание","Ржавый","Семнадцать","Рассвет","Печь","Девять","Добросердечный","Один")
                .max(Comparator.comparing(String::length)).ifPresent(System.out::println);

        System.out.println("--------------");

        //6. Имеется строка с набором слов в нижнем регистре, разделенных пробелом. Постройте хеш-мапы,
        // в которой будут храниться пары: слово - сколько раз оно встречается во входной строке
        String textSeven = "лишь многие известные личности лишь инициированные исключительно синтетически объективно инициированные рассмотрены соответствующими инстанциями";

/*        Stream.of("лишь многие известные личности лишь инициированные исключительно синтетически объективно инициированные рассмотрены соответствующими инстанциями")
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).
                forEach((word, count) -> System.out.println(word + " : " + count));*/    //2 терминальных метода не уверена , что так правильно , хотя компилятор пропускает

        Map<String, Long> wordCount = Stream.of(textSeven)
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        wordCount.forEach((word, count) -> System.out.println(word + " : " + count));

        System.out.println("--------------");

        //7. Отпечатайте в консоль строки из списка в порядке увеличения длины слова, если слова имеют одинаковую длины, то должен быть сохранен алфавитный порядок

        Stream.of("Желание","Ржавый","Семнадцать","Рассвет","Печь","Девять","Добросердечный","Один","Аппп").
                sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())).
                forEach(System.out::println);


        System.out.println("--------------");
        //8. Имеется массив строк, в каждой из которых лежит набор из 5 слов, разделенных пробелом, найдите среди всех слов самое длинное,
        // если таких слов несколько, получите любое из них

        String[] strWords ={
                "Один два три четыре пять",
                "шесть семь восемь девять десять",
                "ддвенадцать одиннадцать восемь девять десять"
        };

        Arrays.stream(strWords).
                flatMap(s -> Arrays.stream(s.split(" ")))
                .max(Comparator.comparing(String::length)).ifPresent(System.out::println);
    }
}
