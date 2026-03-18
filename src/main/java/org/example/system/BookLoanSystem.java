package org.example.system;

import java.time.LocalDateTime;
import java.util.List;

import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.entities.Student;
import org.example.exceptions.BookNotAvailableException;
import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.PendingPenaltyException;
import org.example.repositories.BookLoanRepository;
import org.example.utils.TerminalUtils;

public class BookLoanSystem {
    private static int idCount = 0;
    private static final BookLoanRepository repository = new BookLoanRepository();

    public static BookLoan loanBook(Student student, Book book)
            throws BookNotFoundException, BookNotAvailableException, PendingPenaltyException {

        if (book == null) {
            throw new BookNotFoundException("O livro solicitado não existe no sistema.");
        }

        if (!BookSystem.isAvailable(book)) {
            throw new BookNotAvailableException("O livro \"" + book.getName() + "\" não está disponível no momento.");
        }

        if (student.getPendingPenalty() > 0) {
            throw new PendingPenaltyException(
                    "Você possui uma multa pendente de R$ " + student.getPendingPenalty() +
                            ". Quite o valor antes de realizar um novo empréstimo."
            );
        }

        BookLoan loan = new BookLoan(
                ++idCount,
                student,
                book,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                0.0F
        );

        repository.add(loan);
        student.addBookLoan(loan);
        BookSystem.removeBook(book);

        TerminalUtils.print("O livro \"" + book.getName() + "\" foi emprestado para "
                + student.getName() + " com sucesso. Devolução em 30 dias.");
        TerminalUtils.waitForInput();
        return loan;
    }

    public static void returnBook(Student student, BookLoan loan) {
        float penalty = loan.getPenalty();

        student.removeBookLoan(loan);
        BookSystem.addBook(loan.getBook());

        if (penalty > 0) {
            student.addPendingPenalty(penalty);
            TerminalUtils.print("Livro devolvido com atraso. Multa de R$ " + penalty
                    + " adicionada ao seu perfil. Quite antes do próximo empréstimo.");
        } else {
            TerminalUtils.print("Livro \"" + loan.getBook().getName() + "\" devolvido com sucesso, sem multa.");
        }

        TerminalUtils.waitForInput();
    }

    public static void reRegisterLoan(BookLoan loan) {
        repository.add(loan);
    }

    public static void printAllLoans() {
        List<BookLoan> loans = repository.getAll();

        if (loans.isEmpty()) {
            TerminalUtils.print("Nenhum empréstimo registrado.");
            TerminalUtils.waitForInput();
            return;
        }

        loans.forEach(loan -> {
            System.out.println();
            System.out.println("------------------------------------------");
            System.out.println("ID Empréstimo : " + loan.getId());
            System.out.println("Aluno         : [ID " + loan.getStudent().getId() + "] " + loan.getStudent().getName());
            System.out.println("Livro         : " + loan.getBook().getName() + " - " + loan.getBook().getAuthor());
            System.out.println("Data retirada : " + loan.getInitialDate());
            System.out.println("Devolução     : " + loan.getFinalDate());
            System.out.println("Multa         : R$ " + loan.getPenalty());
            System.out.println("------------------------------------------");
        });

        TerminalUtils.waitForInput();
    }
}