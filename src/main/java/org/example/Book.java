package org.example;
// testee
public class Book {
    private int id;
    private String name;

    Book(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
