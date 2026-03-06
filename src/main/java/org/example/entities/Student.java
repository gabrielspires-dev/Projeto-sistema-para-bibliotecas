package org.example.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student implements SystemUser {
    private int id;
    private String name;
    private String password;
    private ArrayList<BookLoan> bookLoans = new ArrayList<>();

    public Student(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void addBookLoan(BookLoan loan) {
        bookLoans.add(loan);
    }

    public List<BookLoan> getBookLoans() {
        return Collections.unmodifiableList(bookLoans);
    }

    public int getBookLoanQuantity() {
        return bookLoans.size();
    }
}