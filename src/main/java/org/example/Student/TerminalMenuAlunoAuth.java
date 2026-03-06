package org.example.Student;

import com.study.TerminalMainMenu;
import com.study.TerminalMenu;

public class TerminalMenuAlunoAuth {
    public static void print() {
        final TerminalMenu menu = new TerminalMenu("Autenticar como aluno");
        menu.addOption(1, "fazer login", () -> System.out.println("Em breve..."));
        menu.addOption(2, "criar conta", () -> System.out.println("Em breve..."));
        menu.addOption(3, "voltar", () -> TerminalMainMenu.print());
        menu.print();
    }
}
