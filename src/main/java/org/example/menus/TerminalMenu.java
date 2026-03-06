package org.example.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.auth.LibrarianAuth;
import org.example.auth.StudentAuth;
import org.example.entities.SystemUser;
import org.example.utils.TerminalUtils;

public class TerminalMenu {
    private String title;
    private List<String> options = new ArrayList<>();
    private Map<Integer, Runnable> actions = new HashMap<>();

    public TerminalMenu(String title) {
        this.title = title;
    }

    public void addOption(int number, String description, Runnable action) {
        options.add(String.format("Digite %02d para %s", number, description));
        actions.put(number, action);
    }

    public void print(Optional<? extends SystemUser> user) {
        TerminalDecoration.printDecorated(user, () -> {
            System.out.println(title);
            System.out.println("----------------------------------------");
            options.forEach(System.out::println);
        });

        int choice = TerminalUtils.nextInt();
        Runnable action = actions.get(choice);

        if (action != null) {
            action.run();
        } else {
            System.out.println("Opção inválida.");
            TerminalUtils.waitForInput();

            if (StudentAuth.isLogged()) {
                TerminalMenuStudentPage.print();
                return;
            }

            if (LibrarianAuth.isLogged()) {
                TerminalMenuLibrarianPage.print();
                return;
            }

            TerminalMainMenu.print();
        }
    }
}