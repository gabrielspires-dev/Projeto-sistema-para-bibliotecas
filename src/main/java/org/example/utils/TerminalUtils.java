package org.example.utils;

import java.util.Scanner;

public class TerminalUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static void print(String message) {
        System.out.println();
        System.out.println(message);
    }

    public static void waitForInput() {
        System.out.println();
        System.out.println("Digite qualquer número para continuar:");
        scanner.nextInt();
        scanner.nextLine(); // limpa o \n restante
    }

    public static int nextInt() {
        int value = scanner.nextInt();
        scanner.nextLine(); // limpa o \n restante
        return value;
    }

    public static String nextLine() {
        return scanner.nextLine();
    }
}