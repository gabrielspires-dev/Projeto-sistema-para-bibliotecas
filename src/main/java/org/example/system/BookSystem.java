package org.example.system;

import java.util.List;

import org.example.entities.Book;
import org.example.repositories.BookRepository;
import org.example.utils.TerminalUtils;

public class BookSystem {
    private static int idCount = 0;
    private static final BookRepository repository = new BookRepository();

    public static void registerBook() {
        TerminalUtils.print("Digite o nome do livro:");
        String name = TerminalUtils.nextLine();

        TerminalUtils.print("Digite o autor do livro:");
        String author = TerminalUtils.nextLine();

        Book book = new Book(idCount++, name, author);

        if (repository.contains(book)) {
            TerminalUtils.print("Esse livro já existe na biblioteca.");
            TerminalUtils.waitForInput();
            return;
        }

        repository.addBook(book);
        TerminalUtils.print("Livro " + book.getName() + ", do autor: " + book.getAuthor() + ", adicionado na biblioteca.");
        TerminalUtils.waitForInput();
    }

    public static void removeBook(Book book) {
        repository.removeBook(book);
        TerminalUtils.print("Livro " + book.getName() + ", do autor " + book.getAuthor() + " foi removido da biblioteca.");
        TerminalUtils.waitForInput();
    }

    public static void removeBookById(int id) {
        Book book = repository.getById(id);

        if (book == null) {
            TerminalUtils.print("Livro com ID " + id + " não encontrado.");
            TerminalUtils.waitForInput();
            return;
        }

        repository.removeBook(book);
        TerminalUtils.print("Livro " + book.getName() + ", do autor " + book.getAuthor() + " foi removido da biblioteca.");
        TerminalUtils.waitForInput();
    }

    public static void addBook(Book book) {
        repository.addBook(book);
    }

    public static boolean isAvailable(Book book) {
        return repository.contains(book);
    }

    public static Book getById(int id) {
        return repository.getById(id);
    }

    public static List<Book> getAll() {
        return repository.getAll();
    }
}



