package org.example.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.example.entities.*;
import org.example.system.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PersistenceService {

    private static final String DIR = "data/";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, t, ctx) ->
                            new JsonPrimitive(src.format(FMT)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, t, ctx) ->
                            LocalDateTime.parse(json.getAsString(), FMT))
            .setPrettyPrinting()
            .create();

    // ---- SALVAR / CARREGAR TUDO ----

    public static void saveAll() {
        new File(DIR).mkdirs();
        saveBooks();
        saveStudents();
        saveLibrarians();
        saveLoans();
    }

    public static void loadAll() {
        loadBooks();
        loadStudents();
        loadLibrarians();
        loadLoans();
    }

    // ---- LIVROS ----
    // Salva apenas os livros disponíveis (que estão no repositório principal).
    // Os livros atualmente emprestados são salvos dentro de saveLoans().

    private static void saveBooks() {
        write("books.json", BookSystem.getAll());
    }

    private static void loadBooks() {
        List<Book> list = read("books.json", new TypeToken<List<Book>>(){}.getType());
        if (list != null) list.forEach(BookSystem::addBook);
    }

    // ---- BIBLIOTECÁRIOS ----

    private static void saveLibrarians() {
        write("librarians.json", LibrarianSystem.getLibrarianList());
    }

    private static void loadLibrarians() {
        List<Librarian> list = read("librarians.json",
                new TypeToken<List<Librarian>>(){}.getType());
        if (list != null) list.forEach(LibrarianSystem::addLibrarian);
    }

    // ---- ALUNOS ----

    private static void saveStudents() {
        write("students.json", StudentSystem.getStudentList());
    }

    private static void loadStudents() {
        List<Student> list = read("students.json",
                new TypeToken<List<Student>>(){}.getType());
        if (list != null) list.forEach(StudentSystem::addStudent);
    }

    // ---- EMPRÉSTIMOS ----
    // Empréstimos referenciam Book e Student por objetos.
    // Usamos um DTO com IDs para serialização e incluímos o Book completo,
    // pois livros emprestados são removidos do repositório principal.

    private static void saveLoans() {
        List<LoanDTO> dtos = new ArrayList<>();
        for (Student s : StudentSystem.getStudentList()) {
            for (BookLoan loan : s.getBookLoans()) {
                dtos.add(new LoanDTO(loan));
            }
        }
        write("loans.json", dtos);
    }

    private static void loadLoans() {
        List<LoanDTO> dtos = read("loans.json",
                new TypeToken<List<LoanDTO>>(){}.getType());
        if (dtos == null) return;

        for (LoanDTO dto : dtos) {
            Student student = StudentSystem.findById(dto.studentId);

            // O livro emprestado não está no repositório principal (foi removido
            // quando o empréstimo foi criado). Por isso salvamos o Book completo
            // dentro do próprio DTO e o reconstruímos diretamente aqui.
            Book book = dto.book;

            if (student == null || book == null) continue;

            BookLoan loan = new BookLoan(
                    dto.id, student, book,
                    dto.initialDate, dto.finalDate, 0.0F);

            student.addBookLoan(loan);
            BookLoanSystem.reRegisterLoan(loan);
        }
    }

    // ---- UTILITÁRIOS ----

    private static void write(String filename, Object obj) {
        try (Writer w = new FileWriter(DIR + filename)) {
            GSON.toJson(obj, w);
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + filename + ": " + e.getMessage());
        }
    }

    private static <T> T read(String filename, Type type) {
        File f = new File(DIR + filename);
        if (!f.exists()) return null;
        try (Reader r = new FileReader(f)) {
            return GSON.fromJson(r, type);
        } catch (IOException e) {
            System.err.println("Erro ao carregar " + filename + ": " + e.getMessage());
            return null;
        }
    }

    // ---- DTO interno para empréstimos ----
    // Guarda o Book completo em vez de apenas o bookId, porque livros
    // emprestados são removidos do BookRepository e não podem ser
    // encontrados via BookSystem.getById() durante o carregamento.

    private static class LoanDTO {
        int id;
        int studentId;
        Book book;           // objeto completo salvo no JSON
        LocalDateTime initialDate;
        LocalDateTime finalDate;

        LoanDTO(BookLoan loan) {
            this.id          = loan.getId();
            this.studentId   = loan.getStudent().getId();
            this.book        = loan.getBook();
            this.initialDate = loan.getInitialDate();
            this.finalDate   = loan.getFinalDate();
        }
    }
}
