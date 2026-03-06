package org.example.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.entities.BookLoan;

public class BookLoanRepository {
    private ArrayList<BookLoan> loans = new ArrayList<>();

    public void add(BookLoan loan) {
        loans.add(loan);
    }

    public List<BookLoan> getAll() {
        return Collections.unmodifiableList(loans);
    }
}
