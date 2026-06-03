package org.example;

import org.example.entities.Librarian;
import org.example.repositories.LibrarianDAO;
import org.example.system.LibrarianSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LibrarianSystem — testes unitários")
class LibrarianSystemTest {

    @Mock
    private LibrarianDAO mockRepo;

    @BeforeEach
    void injectMock() {
        LibrarianSystem.setRepository(mockRepo);
    }

    @AfterEach
    void restoreDefault() {
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

        LibrarianSystem.delete(librarian);

        verify(mockRepo).removeById(1);
    }
}
