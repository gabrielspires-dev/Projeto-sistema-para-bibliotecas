package org.example;

public class Book {
    private int id;
    private String name;
    private boolean availability;

    Book(int id, String name) {
        this.id = id;
        this.name = name;
        this.availability = true;
    }

    public String getName() {
        return this.name;
    }
}
