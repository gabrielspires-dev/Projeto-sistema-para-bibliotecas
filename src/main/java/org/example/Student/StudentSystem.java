package org.example.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// Criar um aluno
// Entregar lista de alunos
// Printar listar de alunos

public class StudentSystem {
    private static int idCount = 0;
    private static ArrayList<Student> studentList = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    
    public static Student createStudent() {
        System.out.println("Digite o seu nome");
        final String studentName = scanner.next();

        System.out.println("Digite sua senha");
        final String studentPassword = scanner.next();

        Student student = new Student(++idCount, studentName, studentPassword);
        System.out.println("O seu ID de acesso é: " + student.getID() + ".");
        System.out.println("Guarde essa informação, você precisará dela para fazer login");

        studentList.add(student);
        return student;
    }

    public static void deleteStudent() {
        System.out.println("Digite o ID que você quer deletar.");
        int idRemove = scanner.nextInt();
        studentList.removeIf(student -> student.getID() == idRemove);
    }

    public static List<Student> getStudentList() {
        return Collections.unmodifiableList(studentList);
    }

    public static void printStudentList() {
        System.out.println("==============================");
        studentList.forEach(Student::printInformation);
        System.out.println("==============================");
    }
}
