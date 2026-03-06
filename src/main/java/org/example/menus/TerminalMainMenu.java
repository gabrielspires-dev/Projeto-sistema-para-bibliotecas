package org.example.menus;

import java.util.Optional;

import org.example.entities.Student;

public class TerminalMainMenu {

    public static void print(Optional<Student> student) {
        TerminalMenu menu = new TerminalMenu("Bem vindo ao Sistema Bibliotecário 1.0");

        menu.addOption(1, "entrar como Aluno", () -> TerminalMenuAlunoAuth.print());
        menu.addOption(2, "entrar como Bibliotecário", () -> System.out.println("Em breve..."));
        menu.addOption(3, "sair", () -> System.exit(0));

        menu.print(student);
    }
}
