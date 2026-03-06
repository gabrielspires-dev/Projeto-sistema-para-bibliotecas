package org.example;

import java.util.Optional;

import org.example.menus.TerminalMainMenu;

public class Main {
    public static void main(String[] args) {
        TerminalMainMenu.print(Optional.empty());
    }
}