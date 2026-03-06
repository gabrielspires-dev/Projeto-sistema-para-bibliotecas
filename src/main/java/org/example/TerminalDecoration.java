package org.example;

import org.example.Student.Student;

import java.util.Optional;

public class TerminalDecoration {
    public static void printDecorated(Optional<Student> student, Runnable content) {

        for (int i = 0; i < 40; i++) System.out.println();

        System.out.println("========================================");
        content.run();
        System.out.println("========================================");

        for (int i = 0; i < 2; i++) System.out.println();

        System.out.println("Sistema Bibliotecário 1.0");
        student.ifPresent(Student::printInformation);

         for (int i = 0; i < 5; i++) System.out.println();
    }
}
