package org.example.menus;

import java.util.Optional;

import org.example.entities.SystemUser;

public class TerminalDecoration {

    public static void printDecorated(Optional<? extends SystemUser> user, Runnable content) {
        for (int i = 0; i < 40; i++) System.out.println();

        System.out.println("========================================");
        content.run();
        System.out.println("========================================");

        System.out.println();
        System.out.println("Sistema Bibliotecário 1.0");
        user.ifPresent(u ->
            System.out.println("ID: " + u.getId() + " | Nome: " + u.getName())
        );

        for (int i = 0; i < 5; i++) System.out.println();
    }
}