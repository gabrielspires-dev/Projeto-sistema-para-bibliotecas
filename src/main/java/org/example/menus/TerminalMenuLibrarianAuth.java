package org.example.menus;

import java.util.Optional;

import org.example.auth.LibrarianAuth;
import org.example.system.BookSystem;

public class TerminalMenuLibrarianAuth {

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Página do bibliotecário");

        if (LibrarianAuth.isLogged()) {
            menu.addOption(1, "cadastrar livro", BookSystem::registerBook);
            menu.addOption(2, "remover livro", BookSystem::removeBook);
            menu.addOption(3, "fazer logout", TerminalMenuLibrarianAuth::logout);
            menu.addOption(4, "voltar", () -> TerminalMainMenu.print());
        } else {
            menu.addOption(1, "fazer login", LibrarianAuth::login);
            menu.addOption(2, "criar conta", TerminalMenuLibrarianAuth::register);
            menu.addOption(3, "voltar", () -> TerminalMainMenu.print());
        }

        menu.print(Optional.empty());
    }

    private static void register() {
        LibrarianAuth.register();
        TerminalMenuLibrarianAuth.print();
    }

    private static void logout() {
        LibrarianAuth.logout();
        TerminalMainMenu.print();
    }
}