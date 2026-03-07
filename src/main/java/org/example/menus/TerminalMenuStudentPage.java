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
            menu.addOption(2, "devolver empréstimo", TerminalMenuStudentPage::listAndReturn);
            menu.addOption(3, "listar seus empréstimos", TerminalMenuStudentPage::listOwnLoans);
            menu.addOption(4, "logout", TerminalMenuStudentPage::logout);
            menu.addOption(5, "deletar conta", TerminalMenuStudentPage::delete);
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
            List<BookLoan> loans = student.getBookLoans();

            if (loans.isEmpty()) {
                TerminalUtils.print("Você não possui empréstimos.");
                TerminalUtils.waitForInput();
                return;
            }

            loans.forEach(loan -> {
                System.out.println();
                System.out.println("------------------------------------------");
                System.out.println("ID Empréstimo : " + loan.getId());
                System.out.println("Livro         : " + loan.getBook().getName() + " - " + loan.getBook().getAuthor());
                System.out.println("Data retirada : " + loan.getInitialDate());
                System.out.println("Devolução     : " + loan.getFinalDate());
                System.out.println("Multa         : R$ " + loan.getPenalty());
                System.out.println("------------------------------------------");
            });

            TerminalUtils.waitForInput();
        });

        TerminalMenuStudentPage.print();
    }

    private static void listAndReturn() {
        StudentAuth.getLoggedStudent().ifPresent(student -> {
            List<BookLoan> loans = student.getBookLoans();

            if (loans.isEmpty()) {
                TerminalUtils.print("Você não possui empréstimos.");
                TerminalUtils.waitForInput();
                TerminalMenuStudentPage.print();
                return;
            }

            loans.forEach(loan -> {
                System.out.println();
                System.out.println("------------------------------------------");
                System.out.println("ID Empréstimo : " + loan.getId());
                System.out.println("Livro         : " + loan.getBook().getName() + " - " + loan.getBook().getAuthor());
                System.out.println("Devolução     : " + loan.getFinalDate());
                System.out.println("Multa         : R$ " + loan.getPenalty());
                System.out.println("------------------------------------------");
            });

            TerminalUtils.print("Digite o ID do empréstimo para devolver (ou -1 para cancelar):");
            int id = TerminalUtils.nextInt();

            if (id == -1) {
                TerminalMenuStudentPage.print();
                return;
            }

            BookLoan loan = loans.stream()
                    .filter(l -> l.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (loan == null) {
                TerminalUtils.print("ID inválido.");
                TerminalUtils.waitForInput();
                TerminalMenuStudentPage.print();
                return;
            }

            BookLoanSystem.returnBook(student, loan);
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