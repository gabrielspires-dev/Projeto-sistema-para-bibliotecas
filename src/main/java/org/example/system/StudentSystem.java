package org.example.system;

import java.util.List;
import java.util.Scanner;

import org.example.entities.Student;
import org.example.repositories.StudentRepository;
import org.example.utils.TerminalUtils;

public class StudentSystem {
    private static int idCount = 0;
    private static final StudentRepository repository = new StudentRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static Student createStudent() {
        System.out.println("Digite o seu nome:");
        final String name = scanner.next();

        System.out.println("Digite sua senha:");
        final String password = scanner.next();

        Student student = new Student(++idCount, name, password);
        repository.add(student);

        TerminalUtils.print("Conta criada! Seu ID de acesso é: " + student.getId() + ". Guarde essa informação.");
        TerminalUtils.waitForInput();
        return student;
    }

    public static void deleteStudent() {
        System.out.println("Digite o ID do aluno que deseja remover:");
        int id = scanner.nextInt();

        Student student = repository.getById(id);

        if (student == null) {
            TerminalUtils.print("Aluno com ID " + id + " não encontrado.");
            TerminalUtils.waitForInput();
            return;
        }

        if (student.getBookLoanQuantity() > 0) {
            TerminalUtils.print("Não é possível remover " + student.getName() + ", pois possui " + student.getBookLoanQuantity() + " empréstimo(s) em aberto.");
            TerminalUtils.waitForInput();
            return;
        }

        repository.removeById(id);
        TerminalUtils.print("Aluno " + student.getName() + " removido com sucesso.");
        TerminalUtils.waitForInput();
    }

    public static List<Student> getStudentList() {
        return repository.getAll();
    }
}