package org.example.system;

import java.util.List;
import java.util.Scanner;

import org.example.entities.Book;
import org.example.repositories.BookRepository;
import org.example.utils.TerminalUtils;

public class BookSystem {
    private static int idCount = 0;
    private static final BookRepository repository = new BookRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static void registerBook() {
        System.out.println("Digite o nome do livro:");
        String name = scanner.nextLine();

        System.out.println("Digite o autor do livro:");
        String author = scanner.nextLine();

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

    public static void removeBook() {
        System.out.println("Digite o ID do livro que deseja remover:");
        int id = scanner.nextInt();

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