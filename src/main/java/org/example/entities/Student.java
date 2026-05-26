package org.example.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student implements SystemUser {
    private int id;
    private String name;
    private String password;
    private ArrayList<BookLoan> bookLoans = new ArrayList<>();
    private float pendingPenalty = 0.0F;

    public Student(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        ensureBookLoans();
    }

    private void ensureBookLoans() {
        if (bookLoans == null) {
            bookLoans = new ArrayList<>();
        }
    }

    @Override
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    @Override
    public String getName() { return name; }

    public String getPassword() { return password; }

    public void addBookLoan(BookLoan loan) {
        ensureBookLoans();
        bookLoans.add(loan);
    }

    public void removeBookLoan(BookLoan loan) {
        ensureBookLoans();
        bookLoans.remove(loan);
    }

    public List<BookLoan> getBookLoans() {
        ensureBookLoans();
        return Collections.unmodifiableList(bookLoans);
    }

    public int getBookLoanQuantity() {
        return bookLoans == null ? 0 : bookLoans.size();
    }

    public float getPendingPenalty() {
        return pendingPenalty;
    }

    public void addPendingPenalty(float value) {
        this.pendingPenalty += value;
    }

    public void payPenalty() {
        this.pendingPenalty = 0.0F;
    }
}