package org.example.auth;

import java.util.Optional;

import org.example.entities.Student;
import org.example.system.StudentSystem;

public class StudentAuth {
    private static Optional<Student> loggedStudent = Optional.empty();

    public static void register() {
        Student student = StudentSystem.createStudent();
        loggedStudent = Optional.of(student);
    }

    public static void login() {
        // TODO: buscar aluno por ID e verificar senha
    }

    public static void logout() {
        loggedStudent = Optional.empty();
    }

    public static boolean isLogged() {
        return loggedStudent.isPresent();
    }

    public static Optional<Student> getLoggedStudent() {
        return loggedStudent;
    }
}