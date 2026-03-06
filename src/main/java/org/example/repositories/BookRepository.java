package org.example.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.entities.Book;

public class BookRepository {
    private ArrayList<Book> storage = new ArrayList<>();

    public void addBook(Book book) {
        storage.add(book);
    }

    public Book removeBook(Book book) {
        storage.remove(book);
        return book;
    }

    public List<Book> getAll() {
        return Collections.unmodifiableList(storage);
    }

    public boolean contains(Book book) {
        for (Book books : storage) {
            if (books.getName().equalsIgnoreCase(book.getName()) && books.getAuthor().equalsIgnoreCase(book.getAuthor())) {
                return true;
            }
        }
        return false;
    }
}
