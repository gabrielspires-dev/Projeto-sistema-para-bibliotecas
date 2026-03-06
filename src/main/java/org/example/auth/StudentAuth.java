package org.example.auth;

import java.util.Optional;

import org.example.entities.Student;
import org.example.system.StudentSystem;
import org.example.utils.TerminalUtils;

public class StudentAuth {
    private static Optional<Student> loggedStudent = Optional.empty();

    public static void register() {
        Student student = StudentSystem.createStudent();
        loggedStudent = Optional.of(student);
    }

    public static boolean login() {
        System.out.println("Digite seu ID:");
        int id = TerminalUtils.nextInt();

        Student student = StudentSystem.findById(id);

        if (student == null) {
            TerminalUtils.print("ID não existe.");
            TerminalUtils.waitForInput();
            return false;
        }

        TerminalUtils.print("Logar como " + student.getName() + "?");
        System.out.println("Digite sua senha:");
        String password = TerminalUtils.nextLine();

        if (!password.equals(student.getPassword())) {
            TerminalUtils.print("Senha incorreta.");
            TerminalUtils.waitForInput();
            return false;
        }

        loggedStudent = Optional.of(student);
        return true;
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