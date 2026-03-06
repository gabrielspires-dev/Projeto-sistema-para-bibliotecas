package org.example;

import java.util.*;

public class TerminalMenu {
    private String title;
    private List<String> options;
    private Map<Integer, Runnable> actions;

    private final static Scanner scanner = new Scanner(System.in);

    public TerminalMenu(String title) {
        this.title = title;
        this.options = new ArrayList<>();
        this.actions = new HashMap<>();
    }

    public void addOption(int number, String description, Runnable action) {
        options.add(String.format("Digite %02d para %s", number, description));
        actions.put(number, action);
    }

    public void print() {
        TerminalDecoration.printDecorated(Optional.empty(), () -> {
            System.out.println(title);
            System.out.println("----------------------------------------");
            System.out.println("Digite suas opções");
            System.out.println();
            options.forEach(System.out::println);
        });

        int choice = scanner.nextInt();
        Runnable action = actions.get(choice);

        if (action != null) {
            action.run();
        } else {
            System.out.println("Opção inválida.");
        }
    }
}
