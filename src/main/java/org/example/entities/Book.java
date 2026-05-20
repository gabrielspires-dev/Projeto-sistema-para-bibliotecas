package org.example.entities;

import com.google.gson.annotations.SerializedName;

public class Book {
    private int id;
    @SerializedName("title")
    private String name;
    private String author;

    public Book(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public int getId() { 
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return this.author;
    }
}

