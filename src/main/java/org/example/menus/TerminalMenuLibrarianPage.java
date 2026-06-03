package org.example.menus;

import org.example.auth.LibrarianAuth;
import org.example.entities.Librarian;
import org.example.entities.Student;
import org.example.system.BookLoanSystem;
import org.example.system.BookSystem;
import org.example.system.LibrarianSystem;
import org.example.system.StudentSystem;
import org.example.utils.TerminalUtils;

public class TerminalMenuLibrarianPage {

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Página do bibliotecário");

        if (LibrarianAuth.isLogged()) {
            menu.addOption(1, "cadastrar livro", TerminalMenuLibrarianPage::registerBook);
            menu.addOption(2, "listar e remover livro", TerminalMenuLibrarianPage::listAndRemoveBook);
            menu.addOption(3, "editar livro", TerminalMenuLibrarianPage::editBook);
            menu.addOption(4, "listar empréstimos", TerminalMenuLibrarianPage::printAllLoans);
            menu.addOption(5, "listar usuários", TerminalMenuLibrarianPage::listAllUsers);
            menu.addOption(6, "editar perfil", TerminalMenuLibrarianPage::editProfile);
            menu.addOption(7, "logout", TerminalMenuLibrarianPage::logout);
            menu.addOption(8, "deletar conta", TerminalMenuLibrarianPage::delete);
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

    private static void editBook() {
        BookSystem.getAll().forEach(
                book -> System.out.println("[ID " + book.getId() + "] " + book.getName() + " - " + book.getAuthor()));

        TerminalUtils.print("Digite o ID do livro para editar (ou -1 para cancelar):");
        int id = TerminalUtils.nextInt();

        if (id == -1) {
            TerminalMenuLibrarianPage.print();
            return;
        }

        BookSystem.editBook(id);
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

    private static void editProfile() {
        LibrarianAuth.getLoggedLibrarian().ifPresent(LibrarianSystem::updateProfile);
        TerminalMenuLibrarianPage.print();
    }

    private static void listAllUsers() {
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("ALUNOS:");
        for (Student s : StudentSystem.getStudentList()) {
            System.out.println("[ID " + s.getId() + "] " + s.getName()
                    + " | " + s.getBookLoanQuantity() + " empréstimo(s)"
                    + " | Multa pendente: R$ " + s.getPendingPenalty());
        }
        System.out.println("------------------------------------------");
        System.out.println("BIBLIOTECÁRIOS:");
        for (Librarian l : LibrarianSystem.getLibrarianList()) {
            System.out.println("[ID " + l.getId() + "] " + l.getName());
        }
        System.out.println("------------------------------------------");
        TerminalUtils.waitForInput();
        TerminalMenuLibrarianPage.print();
    }

    private static void listAndRemoveBook() {
        BookSystem.getAll().forEach(
                book -> System.out.println("[ID " + book.getId() + "] " + book.getName() + " - " + book.getAuthor()));

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