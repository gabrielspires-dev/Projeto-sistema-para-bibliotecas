package org.example.repositories;

import org.example.entities.Book;
import java.util.List;

public interface BookDAO {
    void addBook(Book book);
    Book removeBook(Book book);
    boolean contains(Book book);
    Book getById(int id);
    List<Book> getAll();
    void updateBook(Book book);
}
