package org.example.menus;

import java.util.Optional;
import java.util.Scanner;

import org.example.entities.Student;
import org.example.system.StudentSystem;

public class TerminalMenuAlunoAuth {
    private static final Scanner scanner = new Scanner(System.in);
    private static Optional<Student> authStudent = Optional.empty();

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Autenticar como aluno");

        if (authStudent.isEmpty()) {
            menu.addOption(1, "fazer login", () -> System.out.println("Em breve..."));
            menu.addOption(2, "criar conta", TerminalMenuAlunoAuth::createStudent);
            menu.addOption(3, "voltar", () -> TerminalMainMenu.print(Optional.empty()));
        } else {
            menu.addOption(1, "sair", TerminalMenuAlunoAuth::logout);
            menu.addOption(2, "voltar", () -> TerminalMainMenu.print(authStudent));
        }

        menu.print(authStudent);
    }

    private static void createStudent() {
        Student student = StudentSystem.createStudent();
        authStudent = Optional.of(student);

        System.out.println("Digite qualquer número para continuar:");
        scanner.nextInt();

        TerminalMainMenu.print(authStudent);
    }

    private static void logout() {
        authStudent = Optional.empty();
        TerminalMainMenu.print(authStudent);
    }
}
