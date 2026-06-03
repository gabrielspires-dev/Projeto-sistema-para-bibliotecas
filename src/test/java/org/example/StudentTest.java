package org.example;

import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.entities.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Student — testes de entidade")
class StudentTest {

    private Student student;
    private BookLoan loan;

    @BeforeEach
    void setup() {
        student = new Student(1, "João Silva", "senha123");
        Book book = new Book(1, "Dom Quixote", "Cervantes");
        LocalDateTime now = LocalDateTime.now();
        loan = new BookLoan(1, student, book, now, now.plusDays(30), 0.0F);
    }

    @Test
    @DisplayName("addBookLoan: deve adicionar empréstimo na lista")
    void addBookLoan_deveAdicionarEmprestimoNaLista() {
        student.addBookLoan(loan);

        assertEquals(1, student.getBookLoanQuantity());
        assertTrue(student.getBookLoans().contains(loan));
    }

    @Test
    @DisplayName("removeBookLoan: deve remover empréstimo da lista")
    void removeBookLoan_deveRemoverEmprestimoDaLista() {
        student.addBookLoan(loan);
        student.removeBookLoan(loan);

        assertEquals(0, student.getBookLoanQuantity());
        assertFalse(student.getBookLoans().contains(loan));
    }

    @Test
    @DisplayName("getPendingPenalty: deve retornar zero inicialmente")
    void getPendingPenalty_deveRetornarZeroInicialmente() {
        assertEquals(0.0F, student.getPendingPenalty());
    }

    @Test
    @DisplayName("addPendingPenalty: deve acumular multas")
    void addPendingPenalty_deveAcumularMultas() {
        student.addPendingPenalty(5.0F);
        student.addPendingPenalty(3.5F);

        assertEquals(8.5F, student.getPendingPenalty(), 0.001F);
    }

    @Test
    @DisplayName("payPenalty: deve zerar multa pendente")
    void payPenalty_deveZerarMultaPendente() {
        student.addPendingPenalty(10.0F);
        student.payPenalty();

        assertEquals(0.0F, student.getPendingPenalty());
    }

    @Test
    @DisplayName("getBookLoanQuantity: deve retornar quantidade correta")
    void getBookLoanQuantity_deveRetornarQuantidadeCorreta() {
        assertEquals(0, student.getBookLoanQuantity());

        student.addBookLoan(loan);
        assertEquals(1, student.getBookLoanQuantity());
    }
}
