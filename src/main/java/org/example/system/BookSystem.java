package org.example.system;

import java.util.List;

import org.example.entities.Book;
import org.example.persistence.DbConfig;
import org.example.repositories.BookDAO;
import org.example.repositories.BookRepository;
import org.example.utils.TerminalUtils;

public class BookSystem {
    private static int idCount = 0;
    private static BookDAO repository = new BookRepository();

    /** Para injeção de dependência em testes. */
    public static void setRepository(BookDAO repo) {
        repository = repo;
    }

    public static void registerBook() {
        TerminalUtils.print("Digite o nome do livro:");
        String name = TerminalUtils.nextLine();

        TerminalUtils.print("Digite o autor do livro:");
        String author = TerminalUtils.nextLine();

        // ID temporário. Se Supabase estiver ativo, o banco gera o real via SERIAL
        // e o repositório o aplica via book.setId(dbId).
        Book book = new Book(DbConfig.isConfigured() ? 0 : idCount++, name, author);

        if (repository.contains(book)) {
            TerminalUtils.print("Esse livro já existe na biblioteca.");
            TerminalUtils.waitForInput();
            return;
        }

        repository.addBook(book);
        TerminalUtils.print("Livro " + book.getName() + ", do autor: " + book.getAuthor() + ", adicionado na biblioteca.");
        TerminalUtils.waitForInput();
    }

    /**
     * Remove o livro do acervo exibindo mensagem de confirmação.
     * Use este método em ações diretas do bibliotecário.
     */
    public static void removeBook(Book book) {
        repository.removeBook(book);
        TerminalUtils.print("Livro " + book.getName() + ", do autor " + book.getAuthor() + " foi removido da biblioteca.");
        TerminalUtils.waitForInput();
    }

    /**
     * Remove o livro do acervo silenciosamente, sem exibir mensagem.
     * Use este método em chamadas internas (ex: empréstimo) para evitar
     * mensagens redundantes ao usuário.
     */
    public static void removeBookSilently(Book book) {
        repository.removeBook(book);
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

    public static void editBook(int id) {
        Book book = repository.getById(id);

        if (book == null) {
            TerminalUtils.print("Livro com ID " + id + " não encontrado.");
            TerminalUtils.waitForInput();
            return;
        }

        TerminalUtils.print("Editando livro: " + book.getName() + " - " + book.getAuthor());
        TerminalUtils.print("Novo título (Enter para manter \"" + book.getName() + "\"):");
        String newName = TerminalUtils.nextLineOrDefault(book.getName());

        TerminalUtils.print("Novo autor (Enter para manter \"" + book.getAuthor() + "\"):");
        String newAuthor = TerminalUtils.nextLineOrDefault(book.getAuthor());

        book.setName(newName);
        book.setAuthor(newAuthor);

        repository.updateBook(book);
        TerminalUtils.print("Livro atualizado: " + book.getName() + " - " + book.getAuthor());
        TerminalUtils.waitForInput();
    }
}
