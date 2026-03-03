package org.example;

import java.time.LocalDateTime;

public class BookLoan {
    private int id;
    private Book book;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private float penalty;

    // Construtor - cria e inicializa o objeto
    BookLoan(int id, Book book, LocalDateTime initialDate, LocalDateTime finalDate, float penalty) {
        this.id = id;
        this.book = book;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.penalty = penalty;
    }
}
