package org.example;

import org.example.entities.Librarian;
import org.example.repositories.LibrarianDAO;
import org.example.system.LibrarianSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LibrarianSystem — testes unitários")
class LibrarianSystemTest {

    @Mock
    private LibrarianDAO mockRepo;

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
        originalIn = System.in;
        LibrarianSystem.setRepository(mockRepo);
    }

    @AfterEach
    void restoreDefault() {
        System.setIn(originalIn);
        LibrarianSystem.setRepository(new org.example.repositories.LibrarianRepository());
    }

    @Test
    @DisplayName("createLibrarian: deve criar bibliotecário com nome e senha e adicionar ao repositório")
    void createLibrarian_deveCriarBibliotecarioComNomeESenha() {
        Librarian librarian = new Librarian(1, "Carlos Admin", "admin123");

        mockRepo.add(librarian);

        verify(mockRepo).add(librarian);
        assertEquals("Carlos Admin", librarian.getName());
        assertEquals("admin123", librarian.getPassword());
    }

    @Test
    @DisplayName("findById: deve retornar bibliotecário correto quando encontrado")
    void findById_deveRetornarBibliotecarioCorreto() {
        Librarian librarian = new Librarian(1, "Carlos Admin", "admin123");
        when(mockRepo.getById(1)).thenReturn(librarian);

        Librarian result = LibrarianSystem.findById(1);

        assertNotNull(result);
        assertEquals("Carlos Admin", result.getName());
    }

    @Test
    @DisplayName("findById: deve retornar null quando bibliotecário não encontrado")
    void findById_deveRetornarNullQuandoNaoEncontrado() {
        when(mockRepo.getById(99)).thenReturn(null);

        Librarian result = LibrarianSystem.findById(99);

        assertNull(result);
    }

    @Test
    @DisplayName("delete: deve remover bibliotecário do repositório")
    void delete_deveRemoverBibliotecario() {
        Librarian librarian = new Librarian(1, "Carlos Admin", "admin123");
        setMockInput("1\n");

        LibrarianSystem.delete(librarian);

        verify(mockRepo).removeById(1);
    }
}
