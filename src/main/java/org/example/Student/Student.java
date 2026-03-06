package org.example.Student;

// Deve devolver o nome
// Deve devolver o id
// Printar suas informações

public class Student {
    private int id;
    private String name;
    private String password;

    public Student(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.id;
    }

    public void printInformation() {
        System.out.println("ID Aluno: " + this.id + " | Nome: " + this.name);
    }
    
}