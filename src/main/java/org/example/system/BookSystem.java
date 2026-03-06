package org.example.system;

import java.util.List;
import java.util.Scanner;

import org.example.entities.Book;
import org.example.repositories.BookRepository;

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
            System.out.println("Esse livro já existe na biblioteca.");
            return;
        }

        repository.addBook(book);
        System.out.println("Livro " + book.getName() + ", do autor: " + book.getAuthor() + ", adicionado na biblioteca.");
    }

    public static void removeBook() {
        System.out.println("Digite o ID do livro que deseja remover:");
        int id = scanner.nextInt();

        Book book = repository.getById(id);

        if (book == null) {
            System.out.println("Livro com ID " + id + " não encontrado.");
            return;
        }

        repository.removeBook(book);
        System.out.println("Livro " + book.getName() + ", do autor " + book.getAuthor() + " foi removido da biblioteca.");
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