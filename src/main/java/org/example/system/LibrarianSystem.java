package org.example.system;

import java.util.List;

import org.example.entities.Librarian;
import org.example.repositories.LibrarianRepository;
import org.example.utils.TerminalUtils;

public class LibrarianSystem {
    private static int idCount = 0;
    private static final LibrarianRepository repository = new LibrarianRepository();

    public static Librarian createLibrarian() {
        TerminalUtils.print("Digite o seu nome:");
        final String name = TerminalUtils.nextLine();

        TerminalUtils.print("Digite sua senha:");
        final String password = TerminalUtils.nextLine();

        Librarian librarian = new Librarian(++idCount, name, password);
        repository.add(librarian);

        TerminalUtils.print("Conta criada! Seu ID de acesso é: " + librarian.getId() + ". Guarde essa informação.");
        TerminalUtils.waitForInput();
        return librarian;
    }

    public static Librarian findById(int id) {
        return repository.getById(id);
    }

    public static void delete(Librarian librarian) {
        repository.removeById(librarian.getId());
        TerminalUtils.print("Conta de " + librarian.getName() + " removida com sucesso.");
        TerminalUtils.waitForInput();
    }

    public static List<Librarian> getLibrarianList() {
        return repository.getAll();
    }
}