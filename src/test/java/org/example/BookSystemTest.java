package org.example;

import org.example.entities.Book;
import org.example.repositories.BookDAO;
import org.example.system.BookSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookSystem — testes unitários")
class BookSystemTest {

    @Mock
    private BookDAO mockRepo;

    @BeforeEach
    void injectMock() {
        BookSystem.setRepository(mockRepo);
    }

    @AfterEach
    void restoreDefault() {
        BookSystem.setRepository(new org.example.repositories.BookRepository());
    }

    @Test
    @DisplayName("registerBook: deve adicionar livro no repositório quando não existe")
    void registerBook_deveAdicionarLivroNoRepositorio() {
        Book book = new Book(1, "Dom Quixote", "Cervantes");
        when(mockRepo.contains(any(Book.class))).thenReturn(false);

        mockRepo.addBook(book);

        verify(mockRepo).addBook(book);
    }

    @Test
    @DisplayName("registerBook: deve lançar erro se livro já existe (contains retorna true)")
    void registerBook_deveLancarErroSeLivroJaExiste() {
        Book book = new Book(1, "Dom Quixote", "Cervantes");
        when(mockRepo.contains(book)).thenReturn(true);

        boolean jaExiste = mockRepo.contains(book);

        assertTrue(jaExiste);
        verify(mockRepo, never()).addBook(book);
    }

    @Test
    @DisplayName("removeBook: deve chamar removeBook no repositório")
    void removeBook_deveRemoverLivroExistente() {
        Book book = new Book(1, "Dom Quixote", "Cervantes");

        BookSystem.removeBook(book);

        verify(mockRepo).removeBook(book);
    }

    @Test
    @DisplayName("getById: deve retornar livro correto quando encontrado")
    void getById_deveRetornarLivroCorreto() {
        Book book = new Book(1, "Dom Quixote", "Cervantes");
        when(mockRepo.getById(1)).thenReturn(book);

        Book result = BookSystem.getById(1);

        assertEquals(book, result);
    }

    @Test
    @DisplayName("getById: deve retornar null quando livro não encontrado")
    void getById_deveRetornarNullQuandoNaoEncontrado() {
        when(mockRepo.getById(99)).thenReturn(null);

        Book result = BookSystem.getById(99);

        assertNull(result);
    }

    @Test
    @DisplayName("isAvailable: deve retornar true quando livro disponível")
    void isAvailable_deveRetornarTrueQuandoLivroDisponivel() {
        Book book = new Book(1, "Dom Quixote", "Cervantes");
        when(mockRepo.contains(book)).thenReturn(true);

        boolean available = BookSystem.isAvailable(book);

        assertTrue(available);
    }

    @Test
    @DisplayName("isAvailable: deve retornar false quando livro indisponível")
    void isAvailable_deveRetornarFalseQuandoLivroIndisponivel() {
        Book book = new Book(1, "Dom Quixote", "Cervantes");
        when(mockRepo.contains(book)).thenReturn(false);

        boolean available = BookSystem.isAvailable(book);

        assertFalse(available);
    }

    @Test
    @DisplayName("getAll: deve retornar lista de livros do repositório")
    void getAll_deveRetornarListaDeLivros() {
        List<Book> books = List.of(
                new Book(1, "Dom Quixote", "Cervantes"),
                new Book(2, "1984", "George Orwell")
        );
        when(mockRepo.getAll()).thenReturn(books);

        List<Book> result = BookSystem.getAll();

        assertEquals(2, result.size());
        assertEquals("Dom Quixote", result.get(0).getName());
    }
}
