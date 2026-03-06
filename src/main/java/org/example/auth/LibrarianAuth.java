package org.example.auth;

import java.util.Optional;

import org.example.entities.Librarian;
import org.example.system.LibrarianSystem;

public class LibrarianAuth {
    private static Optional<Librarian> loggedLibrarian = Optional.empty();

    public static void register() {
        Librarian librarian = LibrarianSystem.createLibrarian();
        loggedLibrarian = Optional.of(librarian);
    }

    public static void login() {
        // TODO: buscar bibliotecário por ID e verificar senha
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