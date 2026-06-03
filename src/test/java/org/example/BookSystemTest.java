package org.example;

import org.example.entities.Book;
import org.example.repositories.BookDAO;
import org.example.system.BookSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookSystem — testes unitários")
class BookSystemTest {

    @Mock
    private BookDAO mockRepo;

    private InputStream originalIn;

    private void setMockInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        try {
            java.lang.reflect.Field f = org.example.utils.TerminalUtils.class.getDeclaredField("scanner");
            java.lang.reflect.Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);
            unsafe.putObject(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), new java.util.Scanner(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void injectMock() {
        originalIn = System.in; // salva o System.in original
        BookSystem.setRepository(mockRepo);
    }

    @AfterEach
    void restoreDefault() {
        System.setIn(originalIn); // restaura o System.in
        BookSystem.setRepository(new org.example.repositories.BookRepository());
    }

    @Test
    @DisplayName("registerBook: deve adicionar livro no repositório quando não existe")
    void registerBook_deveAdicionarLivroNoRepositorio() {
        when(mockRepo.contains(any(Book.class))).thenReturn(false);

        // Simula o usuário digitando nome e autor via terminal, mais "1" para o waitForInput
        setMockInput("Dom Quixote\nCervantes\n1\n");

        BookSystem.registerBook();

        verify(mockRepo).addBook(any(Book.class));
    }

    @Test
    @DisplayName("registerBook: não deve adicionar livro se já existe no repositório")
    void registerBook_naoDeveAdicionarSeLivroJaExiste() {
        when(mockRepo.contains(any(Book.class))).thenReturn(true);

        // Simula o usuário digitando nome e autor via terminal, mais "1" para o waitForInput
        setMockInput("Dom Quixote\nCervantes\n1\n");

        BookSystem.registerBook();

        verify(mockRepo, never()).addBook(any(Book.class));
    }

    @Test
    @DisplayName("removeBook: deve chamar removeBook no repositório")
    void removeBook_deveRemoverLivroExistente() {
        Book book = new Book(1, "Dom Quixote", "Cervantes");
        setMockInput("1\n");

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
