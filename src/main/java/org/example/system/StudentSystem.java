package org.example.system;

import java.util.List;

import org.example.entities.Student;
import org.example.persistence.DbConfig;
import org.example.repositories.StudentDAO;
import org.example.repositories.StudentRepository;
import org.example.utils.TerminalUtils;

public class StudentSystem {
    private static int idCount = 0;
    private static StudentDAO repository = new StudentRepository();

    /** Para injeção de dependência em testes. */
    public static void setRepository(StudentDAO repo) {
        repository = repo;
    }

    public static Student createStudent() {
        TerminalUtils.print("Digite o seu nome:");
        final String name = TerminalUtils.nextLine();

        TerminalUtils.print("Digite sua senha:");
        final String password = TerminalUtils.nextLine();

        // ID temporário: se Supabase estiver ativo, o banco gera o ID real (SERIAL)
        // e o repositório o aplica via student.setId(dbId).
        int tempId = DbConfig.isConfigured() ? 0 : ++idCount;
        Student student = new Student(tempId, name, password);
        boolean created = repository.add(student);

        if (created) {
            TerminalUtils.print("Conta criada! Seu ID de acesso é: " + student.getId() + ". Guarde essa informação.");
        } else {
            TerminalUtils.print("Falha ao criar conta. Verifique se as tabelas do Supabase existem e se a conexão está correta.");
        }
        TerminalUtils.waitForInput();
        return student;
    }

    public static Student findById(int id) {
        return repository.getById(id);
    }

    public static void delete(Student student) {
        if (student.getBookLoanQuantity() > 0) {
            TerminalUtils.print("Não é possível remover " + student.getName() + ", pois possui " + student.getBookLoanQuantity() + " empréstimo(s) em aberto.");
            TerminalUtils.waitForInput();
            return;
        }

        repository.removeById(student.getId());
        TerminalUtils.print("Conta de " + student.getName() + " removida com sucesso.");
        TerminalUtils.waitForInput();
    }

    public static void update(Student student) {
        repository.update(student);
    }

    public static void updateProfile(Student student) {
        TerminalUtils.print("Editar perfil de " + student.getName());
        TerminalUtils.print("Novo nome (Enter para manter \"" + student.getName() + "\"):");
        String newName = TerminalUtils.nextLine().trim();

        TerminalUtils.print("Nova senha (Enter para manter a atual):");
        String newPassword = TerminalUtils.nextLine().trim();

        if (!newName.isEmpty()) student.setName(newName);
        if (!newPassword.isEmpty()) student.setPassword(newPassword);

        repository.update(student);
        TerminalUtils.print("Perfil atualizado com sucesso!");
        TerminalUtils.waitForInput();
    }

    // Usado pelo PersistenceService ao carregar dados do disco.
    // Insere o aluno diretamente no repositório sem interação com o terminal
    // e sincroniza o contador de IDs para evitar colisões futuras.
    public static void addStudent(Student student) {
        repository.add(student);
        if (student.getId() >= idCount) {
            idCount = student.getId() + 1;
        }
    }

    public static List<Student> getStudentList() {
        return repository.getAll();
    }
}