package org.example.entities;

public class Book {
    private int id;
    private String name;
    private String author;
    private int quantity;

    public Book(int id, String name, String author, int quantity) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.quantity = quantity;
    }

    public int getId() { 
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
