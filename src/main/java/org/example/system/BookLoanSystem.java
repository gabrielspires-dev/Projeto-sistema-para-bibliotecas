package org.example.system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.entities.Book;
import org.example.entities.BookLoan;

public class BookLoanSystem {
    private static int idCount = 0;
    private static final ArrayList<BookLoan> loans = new ArrayList<>();

    public static BookLoan loanBook(Book book) {
        if (!BookSystem.isAvailable(book)) {
            System.out.println("O livro " + book.getName() + " não está disponível.");
            return null;
        }

        Book retrievedBook = BookSystem.getBook(book);

        BookLoan loan = new BookLoan(
                idCount++,
                retrievedBook,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                0.0F
        );

        loans.add(loan);
        System.out.println("O livro " + book.getName() + " foi emprestado.");
        return loan;
    }

    public static List<BookLoan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    // TODO: returnBook(BookLoan loan) — verificar multa e devolver ao BookSystem
}
