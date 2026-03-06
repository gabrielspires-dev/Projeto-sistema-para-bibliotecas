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

    public boolean contains(Book book) {
        for (Book b : storage) {
            if (b.getName().equalsIgnoreCase(book.getName()) && b.getAuthor().equalsIgnoreCase(book.getAuthor())) {
                return true;
            }
        }
        return false;
    }

    public Book getById(int id) {
        for (Book b : storage) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    public List<Book> getAll() {
        return Collections.unmodifiableList(storage);
    }
}