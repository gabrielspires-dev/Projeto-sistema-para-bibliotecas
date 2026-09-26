package org.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TerminalUtils {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String SEPARATOR = "------------------------------------------";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Imprime uma linha em branco seguida da mensagem. */
    public static void print(String message) {
        System.out.println();
        System.out.println(message);
    }

    /** Imprime o separador padrão reutilizável. */
    public static void printSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Imprime um campo no formato "Rótulo         : Valor", alinhando os valores
     * numa coluna fixa de 14 caracteres.
     *
     * @param label rótulo do campo (ex: "ID Empréstimo")
     * @param value valor a exibir
     */
    public static void printField(String label, Object value) {
        System.out.printf("%-14s: %s%n", label, value);
    }

    /**
     * Formata um {@link LocalDateTime} para o padrão {@code dd/MM/yyyy HH:mm},
     * mais legível do que o toString padrão ISO-8601.
     *
     * @param dateTime data/hora a formatar; retorna "—" se {@code null}
     * @return string formatada
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "\u2014";
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Aguarda o usuário pressionar Enter para continuar.
     * Aceita qualquer entrada de texto sem lançar exceção.
     */
    public static void waitForInput() {
        System.out.println();
        System.out.println("Pressione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Lê um inteiro do terminal. Em caso de entrada inválida, exibe mensagem de
     * erro e repete a leitura até obter um valor numérico válido.
     *
     * @return o inteiro lido
     */
    public static int nextInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // limpa o \n restante
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // descarta a entrada inválida
                System.out.println("Entrada inválida. Digite um número inteiro:");
            }
        }
    }

    /**
     * Lê uma linha do terminal.
     *
     * @return a linha lida (pode ser vazia)
     */
    public static String nextLine() {
        return scanner.nextLine();
    }

    /**
     * Lê uma linha e, se o usuário deixar em branco (apenas Enter), retorna o
     * valor padrão fornecido. Útil para fluxos de edição do tipo
     * "Enter para manter o valor atual".
     *
     * @param defaultValue valor a retornar caso a entrada esteja em branco
     * @return a linha lida ou {@code defaultValue} se vazia
     */
    public static String nextLineOrDefault(String defaultValue) {
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}