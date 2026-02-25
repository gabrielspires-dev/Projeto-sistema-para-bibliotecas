package org.example;

import java.util.ArrayList;

public class Repository {
    private ArrayList<Book> storage = new ArrayList<>();

    public void addBook(Book book) {
        if (isBookInStorage(book)) {
            System.out.println("Esse livro já existe na biblioteca, não foi possível adicionar");
        } else {
            System.out.println("Livro " + book.getName() + " foi adicionado na biblioteca.");
            storage.add(book);
        }

    }

    public Book getBook(Book book) {
        if (isBookInStorage(book)) {
            storage.remove(book);
            System.out.println("O livro " + book.getName() + " foi removido da biblioteca");
            return book;
        } else {
            System.out.println("O livro "  + book.getName() + " Não existe na biblioteca");
            return null;
        }
    }

    public boolean isBookInStorage(Book book) {
        return storage.contains(book);
    }
}
