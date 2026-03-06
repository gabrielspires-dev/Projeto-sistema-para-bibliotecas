package org.example.menus;

import java.util.*;

import org.example.entities.Student;

public class TerminalMenu {
    private String title;
    private List<String> options = new ArrayList<>();
    private Map<Integer, Runnable> actions = new HashMap<>();

    private static final Scanner scanner = new Scanner(System.in);

    public TerminalMenu(String title) {
        this.title = title;
    }

    public void addOption(int number, String description, Runnable action) {
        options.add(String.format("Digite %02d para %s", number, description));
        actions.put(number, action);
    }

    public void print(Optional<Student> student) {
        TerminalDecoration.printDecorated(student, () -> {
            System.out.println(title);
            System.out.println("----------------------------------------");
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
