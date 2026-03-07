package org.example.system;

import java.time.LocalDateTime;
import java.util.List;

import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.entities.Student;
import org.example.repositories.BookLoanRepository;
import org.example.utils.TerminalUtils;

public class BookLoanSystem {
    private static int idCount = 0;
    private static final BookLoanRepository repository = new BookLoanRepository();

    public static BookLoan loanBook(Student student, Book book) {
        if (!BookSystem.isAvailable(book)) {
            TerminalUtils.print("O livro " + book.getName() + ", do autor " + book.getAuthor() + " não está disponível.");
            TerminalUtils.waitForInput();
            return null;
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

        TerminalUtils.print("O livro " + book.getName() + " foi emprestado para " + student.getName() + " com ID " + student.getId() + ".");
        TerminalUtils.waitForInput();
        return loan;
    }

    public static void returnBook(Student student, BookLoan loan) {
        float penalty = loan.getPenalty();

        student.removeBookLoan(loan);
        BookSystem.addBook(loan.getBook());

        if (penalty > 0) {
            TerminalUtils.print("Livro devolvido com atraso. Multa: R$ " + penalty);
        } else {
            TerminalUtils.print("Livro \"" + loan.getBook().getName() + "\" devolvido com sucesso, sem multa.");
        }

        TerminalUtils.waitForInput();
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