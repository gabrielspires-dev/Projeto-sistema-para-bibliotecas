package org.example.menus;

import java.util.List;

import org.example.auth.StudentAuth;
import org.example.entities.Book;
import org.example.system.BookLoanSystem;
import org.example.system.BookSystem;
import org.example.system.StudentSystem;
import org.example.utils.TerminalUtils;

public class TerminalMenuAlunoAuth {

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Página do aluno");

        if (StudentAuth.isLogged()) {
            menu.addOption(1, "pegar livro emprestado", TerminalMenuAlunoAuth::listAndLoan);
            menu.addOption(2, "logout", TerminalMenuAlunoAuth::logout);
            menu.addOption(3, "deletar conta", TerminalMenuAlunoAuth::delete);
            menu.addOption(4, "voltar", () -> TerminalMainMenu.print());
        } else {
            menu.addOption(1, "fazer login", StudentAuth::login);
            menu.addOption(2, "criar conta", TerminalMenuAlunoAuth::register);
            menu.addOption(3, "voltar", () -> TerminalMainMenu.print());
        }

        menu.print(StudentAuth.getLoggedStudent());
    }

    private static void register() {
        StudentAuth.register();
        TerminalMenuAlunoAuth.print();
    }

    private static void logout() {
        StudentAuth.logout();
        TerminalMainMenu.print();
    }

    private static void delete() {
        StudentSystem.deleteStudent();
    }

    private static void listAndLoan() {
        List<Book> books = BookSystem.getAll();

        if (books.isEmpty()) {
            TerminalUtils.print("Nenhum livro disponível.");
            TerminalUtils.waitForInput();
            TerminalMenuAlunoAuth.print();
            return;
        }

        books.forEach(book ->
            System.out.println("[ID " + book.getId() + "] " + book.getName() + " - " + book.getAuthor())
        );

        System.out.println("Digite o ID para pegar emprestado (ou -1 para cancelar):");
        int id = TerminalUtils.nextInt();

        if (id == -1) {
            TerminalMenuAlunoAuth.print();
            return;
        }

        Book book = BookSystem.getById(id);

        if (book == null) {
            TerminalUtils.print("ID inválido.");
            TerminalUtils.waitForInput();
            TerminalMenuAlunoAuth.print();
            return;
        }

        StudentAuth.getLoggedStudent().ifPresent(student ->
            BookLoanSystem.loanBook(student, book)
        );

        TerminalMenuAlunoAuth.print();
    }
}