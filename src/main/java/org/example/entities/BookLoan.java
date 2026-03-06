package org.example.entities;

import java.time.LocalDateTime;

public class BookLoan {
    private int id;
    private Book book;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private float penalty;

    public BookLoan(int id, Book book, LocalDateTime initialDate, LocalDateTime finalDate, float penalty) {
        this.id = id;
        this.book = book;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.penalty = penalty;
    }

    public int getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public LocalDateTime getInitialDate() {
        return initialDate;
    }

    public LocalDateTime getFinalDate() {
        return finalDate;
    }

    public float getPenalty() {
        return penalty;
    }
}
