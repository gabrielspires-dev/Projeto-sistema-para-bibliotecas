package org.example.menus;

import java.util.Optional;

import org.example.entities.Student;

public class TerminalDecoration {

    public static void printDecorated(Optional<Student> student, Runnable content) {
        for (int i = 0; i < 40; i++) System.out.println();

        System.out.println("========================================");
        content.run();
        System.out.println("========================================");

        System.out.println();
        System.out.println("Sistema Bibliotecário 1.0");
        student.ifPresent(s ->
            System.out.println("ID: " + s.getId() + " | Nome: " + s.getName() + " | Empréstimos: " + s.getBookLoanQuantity())
        );

        for (int i = 0; i < 5; i++) System.out.println();
    }
}