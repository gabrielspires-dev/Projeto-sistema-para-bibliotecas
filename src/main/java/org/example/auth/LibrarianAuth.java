package org.example.auth;

public class LibrarianAuth {
    private static boolean logged = false;

    public static void login() {
        // TODO: autenticação do bibliotecário
    }

    public static void logout() {
        logged = false;
    }

    public static boolean isLogged() {
        return logged;
    }
}