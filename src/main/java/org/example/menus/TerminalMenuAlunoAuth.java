package org.example.menus;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.example.auth.StudentAuth;
import org.example.entities.Book;
import org.example.system.BookLoanSystem;
import org.example.system.BookSystem;

public class TerminalMenuAlunoAuth {
    private static final Scanner scanner = new Scanner(System.in);

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Página do aluno");

        if (StudentAuth.isLogged()) {
            menu.addOption(1, "pegar livro emprestado", TerminalMenuAlunoAuth::listAndLoan);
            menu.addOption(2, "fazer logout", TerminalMenuAlunoAuth::logout);
            menu.addOption(3, "voltar", () -> TerminalMainMenu.print(StudentAuth.getLoggedStudent()));
        } else {
            menu.addOption(1, "fazer login", StudentAuth::login);
            menu.addOption(2, "criar conta", TerminalMenuAlunoAuth::register);
            menu.addOption(3, "voltar", () -> TerminalMainMenu.print(Optional.empty()));
        }

        menu.print(StudentAuth.getLoggedStudent());
    }

    private static void register() {
        StudentAuth.register();
        TerminalMenuAlunoAuth.print();
    }

    private static void logout() {
        StudentAuth.logout();
        TerminalMainMenu.print(Optional.empty());
    }

    private static void listAndLoan() {
        List<Book> books = BookSystem.getAll();

        if (books.isEmpty()) {
            System.out.println("Nenhum livro disponível.");
            System.out.println("Digite qualquer número para continuar:");
            scanner.nextInt();
            TerminalMenuAlunoAuth.print();
            return;
        }

        books.forEach(book ->
            System.out.println("[ID " + book.getId() + "] " + book.getName() + " - " + book.getAuthor())
        );

        System.out.println("Digite o ID para pegar emprestado (ou -1 para cancelar):");
        int id = scanner.nextInt();

        if (id == -1) {
            TerminalMenuAlunoAuth.print();
            return;
        }

        Book book = BookSystem.getById(id);

        if (book == null) {
            System.out.println("ID inválido.");
            System.out.println("Digite qualquer número para continuar:");
            scanner.nextInt();
            TerminalMenuAlunoAuth.print();
            return;
        }

        StudentAuth.getLoggedStudent().ifPresent(student ->
            BookLoanSystem.loanBook(student, book)
        );

        System.out.println("Digite qualquer número para continuar:");
        scanner.nextInt();
        TerminalMenuAlunoAuth.print();
    }
}