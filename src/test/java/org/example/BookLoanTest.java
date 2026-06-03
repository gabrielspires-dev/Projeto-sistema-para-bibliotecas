package org.example;

import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.entities.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookLoan — testes de entidade")
class BookLoanTest {

    private Student student;
    private Book book;

    @BeforeEach
    void setup() {
        student = new Student(1, "João Silva", "senha123");
        book = new Book(1, "Dom Quixote", "Cervantes");
    }

    @Test
    @DisplayName("getPenalty: deve retornar zero quando dentro do prazo")
    void getPenalty_deveRetornarZeroQuandoDentroDoPrazo() {
        LocalDateTime now = LocalDateTime.now();
        BookLoan loan = new BookLoan(1, student, book, now, now.plusDays(30), 0.0F);

        assertEquals(0.0F, loan.getPenalty());
    }

    @Test
    @DisplayName("getPenalty: deve retornar multa fixa mais multa por dia quando atrasado")
    void getPenalty_deveRetornarMultaFixaMaisMultaPorDiaQuandoAtrasado() {
        LocalDateTime past = LocalDateTime.now().minusDays(5);
        LocalDateTime due = past.plusDays(1); // venceu 4 dias atrás
        BookLoan loan = new BookLoan(1, student, book, past, due, 0.0F);

        float penalty = loan.getPenalty();
        // 4 dias de atraso: 4.0 + (4 * 0.5) = 6.0
        assertTrue(penalty >= 4.0F + (3 * 0.5F), "Multa deve ser >= 5.5 para atraso de pelo menos 3 dias");
    }

    @Test
    @DisplayName("getPenalty: deve retornar multa mínima quando atrasado um dia (R$ 4.50)")
    void getPenalty_deveRetornarMultaMinimaQuandoAtrasadoUmDia() {
        // Colocar data de devolução bem no passado para garantir 1+ dias de atraso
        LocalDateTime past = LocalDateTime.now().minusDays(2);
        LocalDateTime due = past.plusDays(1); // venceu há pelo menos 1 dia
        BookLoan loan = new BookLoan(1, student, book, past, due, 0.0F);

        float penalty = loan.getPenalty();
        // Ao menos 1 dia de atraso: 4.0 + (1 * 0.5) = 4.5
        assertEquals(4.5F, penalty, 0.01F);
    }
}
