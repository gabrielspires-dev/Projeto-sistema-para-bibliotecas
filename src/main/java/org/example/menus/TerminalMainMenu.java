package org.example.menus;

import java.util.Optional;

public class TerminalMainMenu {

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Bem vindo ao Sistema Bibliotecário 1.0");

        menu.addOption(1, "entrar como Aluno", () -> TerminalMenuStudentPage.print());
        menu.addOption(2, "entrar como Bibliotecário", () -> TerminalMenuLibrarianPage.print());
        menu.addOption(3, "sair", () -> System.exit(0));

        menu.print(Optional.empty());
    }
}