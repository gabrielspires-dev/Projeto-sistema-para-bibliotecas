package org.example.system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.entities.Student;

public class BookLoanSystem {
    private static int idCount = 0;
    private static final ArrayList<BookLoan> loans = new ArrayList<>();

    public static BookLoan loanBook(Student student, Book book) {
        if (!BookSystem.isAvailable(book)) {
            System.out.println("O livro " + book.getName() + ", do autor " + book.getAuthor() + " não está disponível.");
            return null;
        }

        BookLoan loan = new BookLoan(
                idCount++,
                student.getId(),
                book,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                0.0F
        );

        loans.add(loan);
        student.addBookLoan(loan);

        System.out.println("O livro " + book.getName() + " foi emprestado para " + student.getName() + ".");
        return loan;
    }

    public static List<BookLoan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    // TODO: returnBook(BookLoan loan) — verificar multa e devolver ao BookSystem
}