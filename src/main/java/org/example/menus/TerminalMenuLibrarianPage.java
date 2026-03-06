package org.example.menus;

import org.example.auth.LibrarianAuth;
import org.example.system.BookLoanSystem;
import org.example.system.BookSystem;
import org.example.system.LibrarianSystem;
import org.example.utils.TerminalUtils;

public class TerminalMenuLibrarianPage {

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Página do bibliotecário");

        if (LibrarianAuth.isLogged()) {
            menu.addOption(1, "cadastrar livro", TerminalMenuLibrarianPage::registerBook);
            menu.addOption(2, "listar e remover livro", TerminalMenuLibrarianPage::listAndRemoveBook);
            menu.addOption(3, "listar empréstimos", TerminalMenuLibrarianPage::printAllLoans);
            menu.addOption(4, "logout", TerminalMenuLibrarianPage::logout);
            menu.addOption(5, "deletar conta", TerminalMenuLibrarianPage::delete);
        } else {
            menu.addOption(1, "fazer login", TerminalMenuLibrarianPage::login);
            menu.addOption(2, "criar conta", TerminalMenuLibrarianPage::register);
            menu.addOption(3, "voltar", () -> TerminalMainMenu.print());
        }

        menu.print(LibrarianAuth.getLoggedLibrarian());
    }

    private static void printAllLoans() {
        BookLoanSystem.printAllLoans();
        TerminalMenuLibrarianPage.print();
    }

    private static void registerBook() {
        BookSystem.registerBook();
        TerminalMenuLibrarianPage.print();
    }

    private static void login() {
        LibrarianAuth.login();
        TerminalMenuLibrarianPage.print();
    }

    private static void register() {
        LibrarianAuth.register();
        TerminalMenuLibrarianPage.print();
    }

    private static void logout() {
        LibrarianAuth.logout();
        TerminalMainMenu.print();
    }

    private static void delete() {
        LibrarianAuth.getLoggedLibrarian().ifPresent(librarian -> {
            LibrarianAuth.logout();
            LibrarianSystem.delete(librarian);
        });
        TerminalMainMenu.print();
    }

    private static void listAndRemoveBook() {
        BookSystem.getAll().forEach(book ->
            System.out.println("[ID " + book.getId() + "] " + book.getName() + " - " + book.getAuthor())
        );

        TerminalUtils.print("Digite o ID para remover (ou -1 para cancelar):");
        int id = TerminalUtils.nextInt();

        if (id == -1) {
            TerminalMenuLibrarianPage.print();
            return;
        }

        BookSystem.removeBookById(id);
        TerminalMenuLibrarianPage.print();
    }
}