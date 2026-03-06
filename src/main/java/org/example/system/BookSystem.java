package org.example.system;

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

        System.out.println("Digite a quantidade:");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // limpa o buffer após nextInt

        Book book = new Book(idCount++, name, author, quantity);

        if (repository.contains(book)) {
            System.out.println("Esse livro já existe na biblioteca.");
            return;
        }

        repository.addBook(book);
        System.out.println("Livro " + book.getName() + ", do autor: " + book.getAuthor() + ", adicionado na biblioteca.");
        System.out.println("Quantidade adicionada: " + book.getQuantity());
    }

    public static void removeBook(Book book) {
        if (!repository.contains(book)) {
            System.out.println("O livro " + book.getName() + " não existe na biblioteca.");
            return;
        }

        repository.removeBook(book);
        System.out.println("Livro " + book.getName() + ", do autor " + book.getAuthor() + " foi removido da biblioteca.");
    }

    public static boolean isAvailable(Book book) {
        return repository.contains(book);
    }

    public static Book getBook(Book book) {
        return repository.removeBook(book);
    }
}