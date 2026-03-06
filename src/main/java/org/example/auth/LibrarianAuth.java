package org.example.auth;

import java.util.Optional;

import org.example.entities.Librarian;
import org.example.system.LibrarianSystem;
import org.example.utils.TerminalUtils;

public class LibrarianAuth {
    private static Optional<Librarian> loggedLibrarian = Optional.empty();

    public static void register() {
        Librarian librarian = LibrarianSystem.createLibrarian();
        loggedLibrarian = Optional.of(librarian);
    }

    public static boolean login() {
        System.out.println("Digite seu ID:");
        int id = TerminalUtils.nextInt();

        Librarian librarian = LibrarianSystem.findById(id);

        if (librarian == null) {
            TerminalUtils.print("ID não existe.");
            TerminalUtils.waitForInput();
            return false;
        }

        TerminalUtils.print("Logar como " + librarian.getName() + "?");
        System.out.println("Digite sua senha:");
        String password = TerminalUtils.nextLine();

        if (!password.equals(librarian.getPassword())) {
            TerminalUtils.print("Senha incorreta.");
            TerminalUtils.waitForInput();
            return false;
        }

        loggedLibrarian = Optional.of(librarian);
        return true;
    }

    public static void logout() {
        loggedLibrarian = Optional.empty();
    }

    public static boolean isLogged() {
        return loggedLibrarian.isPresent();
    }

    public static Optional<Librarian> getLoggedLibrarian() {
        return loggedLibrarian;
    }
}