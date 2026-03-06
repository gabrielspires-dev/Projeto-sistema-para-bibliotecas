package org.example.entities;

public class Librarian implements SystemUser {
    private int id;
    private String name;
    private String password;

    public Librarian(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}