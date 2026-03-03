package org.example;

import java.time.LocalDateTime;

public class Library {
    private int idCount = 0;
    Repository repository = new Repository();

    public BookLoan getBook(Book book) {
        if (repository.isBookInStorage(book)) {

            Book newBook = new Book(
                    idCount,
                    "Harry Potter"
            );

            BookLoan emprestimo = new BookLoan(
                    idCount,
                    newBook,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(5),
                    0.0F
                    );

            idCount++;
            System.out.println("O livro " + book.getName() + " foi pego da biblioteca");
            return emprestimo;
        } else {
            System.out.println("Não existe o livro " + book.getName() + " na biblioteca");
            return null;
        }
    }

    // receber o livro
        // precisa verificar se há multa

}
