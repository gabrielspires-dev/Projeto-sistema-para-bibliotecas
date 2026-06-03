package org.example.system;

import java.util.List;

import org.example.entities.Librarian;
import org.example.persistence.DbConfig;
import org.example.repositories.LibrarianDAO;
import org.example.repositories.LibrarianRepository;
import org.example.utils.TerminalUtils;

public class LibrarianSystem {
    private static int idCount = 0;
    private static LibrarianDAO repository = new LibrarianRepository();

    /** Para injeção de dependência em testes. */
    public static void setRepository(LibrarianDAO repo) {
        repository = repo;
    }

    public static Librarian createLibrarian() {
        TerminalUtils.print("Digite o seu nome:");
        final String name = TerminalUtils.nextLine();

        TerminalUtils.print("Digite sua senha:");
        final String password = TerminalUtils.nextLine();

        // ID temporário: se Supabase estiver ativo, o banco gera o ID real (SERIAL)
        // e o repositório o aplica via librarian.setId(dbId).
        int tempId = DbConfig.isConfigured() ? 0 : ++idCount;
        Librarian librarian = new Librarian(tempId, name, password);
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

    public static void updateProfile(Librarian librarian) {
        TerminalUtils.print("Editar perfil de " + librarian.getName());
        TerminalUtils.print("Novo nome (Enter para manter \"" + librarian.getName() + "\"):");
        String newName = TerminalUtils.nextLine().trim();

        TerminalUtils.print("Nova senha (Enter para manter a atual):");
        String newPassword = TerminalUtils.nextLine().trim();

        if (!newName.isEmpty()) librarian.setName(newName);
        if (!newPassword.isEmpty()) librarian.setPassword(newPassword);

        repository.update(librarian);
        TerminalUtils.print("Perfil atualizado com sucesso!");
        TerminalUtils.waitForInput();
    }

    // Usado pelo PersistenceService ao carregar dados do disco.
    // Insere o bibliotecário diretamente no repositório sem interação com o terminal
    // e sincroniza o contador de IDs para evitar colisões futuras.
    public static void addLibrarian(Librarian librarian) {
        repository.add(librarian);
        if (librarian.getId() >= idCount) {
            idCount = librarian.getId() + 1;
        }
    }

    public static List<Librarian> getLibrarianList() {
        return repository.getAll();
    }
}