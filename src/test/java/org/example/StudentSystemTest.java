package org.example;

import org.example.entities.Student;
import org.example.repositories.StudentDAO;
import org.example.system.StudentSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentSystem — testes unitários")
class StudentSystemTest {

    @Mock
    private StudentDAO mockRepo;

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
        StudentSystem.setRepository(mockRepo);
    }

    @AfterEach
    void restoreDefault() {
        System.setIn(originalIn);
        StudentSystem.setRepository(new org.example.repositories.StudentRepository());
    }

    @Test
    @DisplayName("createStudent: deve criar aluno com nome e senha e adicionar ao repositório")
    void createStudent_deveCriarAlunoComNomeESenha() {
        Student student = new Student(1, "João Silva", "senha123");
        when(mockRepo.add(any(Student.class))).thenReturn(true);

        boolean result = mockRepo.add(student);

        assertTrue(result);
        assertEquals("João Silva", student.getName());
        assertEquals("senha123", student.getPassword());
    }

    @Test
    @DisplayName("findById: deve retornar aluno correto quando encontrado")
    void findById_deveRetornarAlunoCorreto() {
        Student student = new Student(1, "João Silva", "senha123");
        when(mockRepo.getById(1)).thenReturn(student);

        Student result = StudentSystem.findById(1);

        assertNotNull(result);
        assertEquals("João Silva", result.getName());
    }

    @Test
    @DisplayName("findById: deve retornar null quando aluno não encontrado")
    void findById_deveRetornarNullQuandoNaoEncontrado() {
        when(mockRepo.getById(99)).thenReturn(null);

        Student result = StudentSystem.findById(99);

        assertNull(result);
    }

    @Test
    @DisplayName("delete: deve remover aluno sem empréstimos ativos")
    void delete_deveRemoverAlunoSemEmprestimos() {
        Student student = new Student(1, "João Silva", "senha123");
        setMockInput("1\n");

        StudentSystem.delete(student);

        verify(mockRepo).removeById(1);
    }

    @Test
    @DisplayName("delete: não deve remover aluno com empréstimos ativos")
    void delete_naoDeveRemoverAlunoComEmprestimosAtivos() {
        Student student = spy(new Student(1, "João Silva", "senha123"));
        when(student.getBookLoanQuantity()).thenReturn(2);
        setMockInput("1\n");

        StudentSystem.delete(student);

        verify(mockRepo, never()).removeById(anyInt());
    }

    @Test
    @DisplayName("update: deve chamar update no repositório com dados do aluno")
    void update_deveAtualizarDadosDoAluno() {
        Student student = new Student(1, "João Silva", "senha123");
        student.addPendingPenalty(5.0F);

        StudentSystem.update(student);

        verify(mockRepo).update(student);
    }
}
