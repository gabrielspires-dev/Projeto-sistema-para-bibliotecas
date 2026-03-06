package org.example.system;

import java.util.List;

import org.example.entities.Student;
import org.example.repositories.StudentRepository;
import org.example.utils.TerminalUtils;

public class StudentSystem {
    private static int idCount = 0;
    private static final StudentRepository repository = new StudentRepository();

    public static Student createStudent() {
        TerminalUtils.print("Digite o seu nome:");
        final String name = TerminalUtils.nextLine();

        TerminalUtils.print("Digite sua senha:");
        final String password = TerminalUtils.nextLine();

        Student student = new Student(++idCount, name, password);
        repository.add(student);

        TerminalUtils.print("Conta criada! Seu ID de acesso é: " + student.getId() + ". Guarde essa informação.");
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

    public static List<Student> getStudentList() {
        return repository.getAll();
    }
}