package org.example.menus;

import java.util.List;

import org.example.auth.StudentAuth;
import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.system.BookLoanSystem;
import org.example.system.BookSystem;
import org.example.system.StudentSystem;
import org.example.utils.TerminalUtils;

public class TerminalMenuStudentPage {

    public static void print() {
        TerminalMenu menu = new TerminalMenu("Página do aluno");

        if (StudentAuth.isLogged()) {
            menu.addOption(1, "pegar livro emprestado", TerminalMenuStudentPage::listAndLoan);
            menu.addOption(2, "listar seus empréstimos", TerminalMenuStudentPage::listOwnLoans);
            menu.addOption(3, "logout", TerminalMenuStudentPage::logout);
            menu.addOption(4, "deletar conta", TerminalMenuStudentPage::delete);
        } else {
            menu.addOption(1, "fazer login", TerminalMenuStudentPage::login);
            menu.addOption(2, "criar conta", TerminalMenuStudentPage::register);
            menu.addOption(3, "voltar", () -> TerminalMainMenu.print());
        }

        menu.print(StudentAuth.getLoggedStudent());
    }

    private static void login() {
        StudentAuth.login();
        TerminalMenuStudentPage.print();
    }

    private static void register() {
        StudentAuth.register();
        TerminalMenuStudentPage.print();
    }

    private static void logout() {
        StudentAuth.logout();
        TerminalMainMenu.print();
    }

    private static void delete() {
        StudentAuth.getLoggedStudent().ifPresent(student -> {
            StudentAuth.logout();
            StudentSystem.delete(student);
        });
        TerminalMainMenu.print();
    }

    private static void listOwnLoans() {
        StudentAuth.getLoggedStudent().ifPresent(student -> {
            List<BookLoan> ownBookLoans =  student.getBookLoans();

            ownBookLoans.forEach(ownBookLoan -> {
                System.out.println();
                System.out.println("------------------------------------------");
                System.out.println("ID Empréstimo : " + ownBookLoan.getId());
                System.out.println("Livro         : " + ownBookLoan.getBook().getName() + " - " + ownBookLoan.getBook().getAuthor());
                System.out.println("Data retirada : " + ownBookLoan.getInitialDate());
                System.out.println("Devolução     : " + ownBookLoan.getFinalDate());
                System.out.println("Multa         : R$ " + ownBookLoan.getPenalty());
                System.out.println("------------------------------------------");
            });

            TerminalUtils.waitForInput();
        });

        TerminalMenuStudentPage.print();
    }

    private static void listAndLoan() {
        List<Book> books = BookSystem.getAll();

        if (books.isEmpty()) {
            TerminalUtils.print("Nenhum livro disponível.");
            TerminalUtils.waitForInput();
            TerminalMenuStudentPage.print();
            return;
        }

        books.forEach(book ->
            System.out.println("[ID " + book.getId() + "] " + book.getName() + " - " + book.getAuthor())
        );

        System.out.println("Digite o ID para pegar emprestado (ou -1 para cancelar):");
        int id = TerminalUtils.nextInt();

        if (id == -1) {
            TerminalMenuStudentPage.print();
            return;
        }

        Book book = BookSystem.getById(id);

        if (book == null) {
            TerminalUtils.print("ID inválido.");
            TerminalUtils.waitForInput();
            TerminalMenuStudentPage.print();
            return;
        }

        StudentAuth.getLoggedStudent().ifPresent(student ->
            BookLoanSystem.loanBook(student, book)
        );

        TerminalMenuStudentPage.print();
    }
}