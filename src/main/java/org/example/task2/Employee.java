package org.example.task2;

public class Employee {
    enum Position {
        ENGINEER, DIRECTOR, MANAGER
    }

    private final String name;
    private final int age;
    private final Position position;

    public Employee(String name, int age, Position position) {
        this.name = name;
        this.age = age;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Position getPosition() {
        return position;
    }
}
