package org.example.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BookLoan {
    private int id;
    private Student student;
    private Book book;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private float penalty;

    public BookLoan(int id, Student student, Book book, LocalDateTime initialDate, LocalDateTime finalDate, float penalty) {
        this.id = id;
        this.student = student;
        this.book = book;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.penalty = penalty;
    }

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
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
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(finalDate)) {
            return 0.0F;
        }

        long daysLate = ChronoUnit.DAYS.between(finalDate, now);
        return 4.0F + (daysLate * 0.5F);
    }
}
