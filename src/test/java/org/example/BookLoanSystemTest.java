package org.example;

import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.entities.Student;
import org.example.exceptions.BookNotAvailableException;
import org.example.exceptions.BookNotFoundException;
import org.example.exceptions.PendingPenaltyException;
import org.example.repositories.BookDAO;
import org.example.repositories.BookLoanDAO;
import org.example.repositories.StudentDAO;
import org.example.system.BookLoanSystem;
import org.example.system.BookSystem;
import org.example.system.StudentSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookLoanSystem — testes unitários")
class BookLoanSystemTest {

    @Mock
    private BookLoanDAO mockLoanRepo;
    @Mock
    private BookDAO mockBookRepo;
    @Mock
    private StudentDAO mockStudentRepo;

    private Student student;
    private Book book;
    private InputStream originalIn;

    @BeforeEach
    void setup() {
        // Redirecionar stdin para não bloquear em waitForInput() / nextInt()
        // "1\n" é suficiente para cada chamada a waitForInput() (que faz nextInt())
        originalIn = System.in;
        System.setIn(new ByteArrayInputStream("1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n".getBytes()));

        BookLoanSystem.setRepository(mockLoanRepo);
        BookSystem.setRepository(mockBookRepo);
        StudentSystem.setRepository(mockStudentRepo);

        student = new Student(1, "João Silva", "senha123");
        book = new Book(1, "Dom Quixote", "Cervantes");
    }

    @AfterEach
    void restoreDefaults() {
        System.setIn(originalIn);
        BookLoanSystem.setRepository(new org.example.repositories.BookLoanRepository());
        BookSystem.setRepository(new org.example.repositories.BookRepository());
        StudentSystem.setRepository(new org.example.repositories.StudentRepository());
    }

    // ---- loanBook ----

    @Test
    @DisplayName("loanBook: deve criar empréstimo com sucesso")
    void loanBook_deveCriarEmprestimoComSucesso() throws Exception {
        when(mockBookRepo.contains(book)).thenReturn(true);

        BookLoan loan = BookLoanSystem.loanBook(student, book);

        assertNotNull(loan);
        assertEquals(student, loan.getStudent());
        assertEquals(book, loan.getBook());
    }

    @Test
    @DisplayName("loanBook: deve lançar BookNotFoundException quando livro é nulo")
    void loanBook_deveLancarBookNotFoundExceptionQuandoLivroNulo() {
        assertThrows(BookNotFoundException.class,
                () -> BookLoanSystem.loanBook(student, null));
    }

    @Test
    @DisplayName("loanBook: deve lançar BookNotAvailableException quando livro indisponível")
    void loanBook_deveLancarBookNotAvailableExceptionQuandoLivroIndisponivel() {
        when(mockBookRepo.contains(book)).thenReturn(false);

        assertThrows(BookNotAvailableException.class,
                () -> BookLoanSystem.loanBook(student, book));
    }

    @Test
    @DisplayName("loanBook: deve lançar PendingPenaltyException quando aluno tem multa")
    void loanBook_deveLancarPendingPenaltyExceptionQuandoAlunoTemMulta() {
        when(mockBookRepo.contains(book)).thenReturn(true);
        student.addPendingPenalty(10.0F);

        assertThrows(PendingPenaltyException.class,
                () -> BookLoanSystem.loanBook(student, book));
    }

    @Test
    @DisplayName("loanBook: deve remover livro do repositório após empréstimo")
    void loanBook_deveRemoverLivroDoRepositorioAposEmprestimo() throws Exception {
        when(mockBookRepo.contains(book)).thenReturn(true);

        BookLoanSystem.loanBook(student, book);

        verify(mockBookRepo).removeBook(book);
    }

    @Test
    @DisplayName("loanBook: deve adicionar empréstimo ao aluno")
    void loanBook_deveAdicionarEmprestimoAoAluno() throws Exception {
        when(mockBookRepo.contains(book)).thenReturn(true);

        BookLoanSystem.loanBook(student, book);

        assertEquals(1, student.getBookLoanQuantity());
    }

    // ---- returnBook ----

    @Test
    @DisplayName("returnBook: deve remover empréstimo do aluno")
    void returnBook_deveRemoverEmprestimoDoAluno() {
        LocalDateTime now = LocalDateTime.now();
        BookLoan loan = new BookLoan(1, student, book, now, now.plusDays(30), 0.0F);
        student.addBookLoan(loan);

        BookLoanSystem.returnBook(student, loan);

        assertEquals(0, student.getBookLoanQuantity());
    }

    @Test
    @DisplayName("returnBook: deve adicionar livro de volta ao repositório")
    void returnBook_deveAdicionarLivroDeVoltaAoRepositorio() {
        LocalDateTime now = LocalDateTime.now();
        BookLoan loan = new BookLoan(1, student, book, now, now.plusDays(30), 0.0F);
        student.addBookLoan(loan);

        BookLoanSystem.returnBook(student, loan);

        verify(mockBookRepo).addBook(book);
    }

    @Test
    @DisplayName("returnBook: deve adicionar multa ao perfil quando atrasado")
    void returnBook_deveAdicionarMultaAoPerfilQuandoAtrasado() {
        LocalDateTime past = LocalDateTime.now().minusDays(35);
        LocalDateTime due = past.plusDays(30); // data de devolução já passou
        BookLoan loan = new BookLoan(1, student, book, past, due, 0.0F);
        student.addBookLoan(loan);

        BookLoanSystem.returnBook(student, loan);

        assertTrue(student.getPendingPenalty() > 0);
        verify(mockStudentRepo).update(student);
    }

    @Test
    @DisplayName("returnBook: não deve adicionar multa quando devolvido no prazo")
    void returnBook_naoDeveAdicionarMultaQuandoNoPrazo() {
        LocalDateTime now = LocalDateTime.now();
        BookLoan loan = new BookLoan(1, student, book, now, now.plusDays(30), 0.0F);
        student.addBookLoan(loan);

        BookLoanSystem.returnBook(student, loan);

        assertEquals(0.0F, student.getPendingPenalty());
        verify(mockStudentRepo, never()).update(any());
    }
}
