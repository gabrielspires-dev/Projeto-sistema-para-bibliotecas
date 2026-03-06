package org.example.utils;

import java.util.Scanner;

public class TerminalUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static void waitForInput() {
        System.out.println();
        System.out.println("Digite qualquer número para continuar:");
        scanner.nextInt();
    }

    public static void print(String message) {
        System.out.println();
        System.out.println(message);
    }

    public static int nextInt() {
        return scanner.nextInt();
    }

    public static String nextLine() {
        return scanner.nextLine();
    }
}