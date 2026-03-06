package org.example.system;

import java.util.List;
import java.util.Scanner;

import org.example.entities.Student;
import org.example.repositories.StudentRepository;

public class StudentSystem {
    private static int idCount = 0;
    private static final StudentRepository repository = new StudentRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static Student createStudent() {
        System.out.println("Digite o seu nome:");
        final String name = scanner.next();

        System.out.println("Digite sua senha:");
        final String password = scanner.next();

        Student student = new Student(++idCount, name, password);
        repository.add(student);

        System.out.println("Conta criada! Seu ID de acesso é: " + student.getId() + ". Guarde essa informação.");
        return student;
    }

    public static void deleteStudent() {
        System.out.println("Digite o ID do aluno que deseja remover:");
        int id = scanner.nextInt();
        repository.removeById(id);
    }

    public static List<Student> getStudentList() {
        return repository.getAll();
    }
}