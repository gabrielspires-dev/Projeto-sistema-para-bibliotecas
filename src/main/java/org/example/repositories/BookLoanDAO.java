package org.example.repositories;

import org.example.entities.BookLoan;
import java.util.List;

public interface BookLoanDAO {
    void add(BookLoan loan);
    void remove(BookLoan loan);
    List<BookLoan> getAll();
}
