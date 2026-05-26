package org.example.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.entities.Book;
import org.example.entities.BookLoan;
import org.example.entities.Student;
import org.example.persistence.DbConfig;
import org.example.persistence.SqliteCache;
import org.example.persistence.SupabaseClient;
import org.example.system.BookSystem;

public class StudentRepository {

    // ---- Supabase: ADD ----
    public boolean add(Student student) {
        if (DbConfig.isConfigured()) {
            JsonObject json = new JsonObject();
            json.addProperty("name", student.getName());
            json.addProperty("password", student.getPassword());
            json.addProperty("pending_penalty", student.getPendingPenalty());
            String response = SupabaseClient.post("students", json.toString());
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                if (array != null && array.size() > 0) {
                    student.setId(array.get(0).getAsJsonObject().get("id").getAsInt());
                    return true;
                }
            }
            return false;
        } else {
            // SQLite cache
            String sql = "INSERT INTO students (name, password, pending_penalty) VALUES (?,?,?)";
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, student.getName());
                ps.setString(2, student.getPassword());
                ps.setDouble(3, student.getPendingPenalty());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) student.setId(rs.getInt(1));
                }
                return true;
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao salvar aluno: " + e.getMessage());
            }
            return false;
        }
    }

    // ---- Supabase: REMOVE ----
    public void removeById(int id) {
        if (DbConfig.isConfigured()) {
            SupabaseClient.delete("students", "?id=eq." + id);
        } else {
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao remover aluno: " + e.getMessage());
            }
        }
    }

    // ---- Supabase: UPDATE ----
    public void update(Student student) {
        if (DbConfig.isConfigured()) {
            JsonObject json = new JsonObject();
            json.addProperty("pending_penalty", student.getPendingPenalty());
            SupabaseClient.patch("students", "?id=eq." + student.getId(), json.toString());
        } else {
            String sql = "UPDATE students SET pending_penalty=? WHERE id=?";
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, student.getPendingPenalty());
                ps.setInt(2, student.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao atualizar aluno: " + e.getMessage());
            }
        }
    }

    // ---- GET BY ID ----
    public Student getById(int id) {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("students", "?id=eq." + id);
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                if (array != null && array.size() > 0) {
                    Student student = SupabaseClient.GSON.fromJson(array.get(0), Student.class);
                    hydrateStudentLoans(student);
                    return student;
                }
            }
            return null;
        }
        // SQLite
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = buildStudentFromRs(rs);
                    hydrateStudentLoans(s);
                    return s;
                }
            }
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao buscar aluno: " + e.getMessage());
        }
        return null;
    }

    // ---- GET ALL ----
    public List<Student> getAll() {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("students", "?order=id.asc");
            if (response != null) {
                Student[] arr = SupabaseClient.GSON.fromJson(response, Student[].class);
                List<Student> list = new ArrayList<>();
                if (arr != null) {
                    for (Student s : arr) { hydrateStudentLoans(s); list.add(s); }
                }
                return list;
            }
        }
        // SQLite
        List<Student> list = new ArrayList<>();
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM students ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student s = buildStudentFromRs(rs);
                hydrateStudentLoans(s);
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao listar alunos: " + e.getMessage());
        }
        return Collections.unmodifiableList(list);
    }

    // ---- Hydration: carrega empréstimos do aluno ----
    private void hydrateStudentLoans(Student student) {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("loans", "?student_id=eq." + student.getId());
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                if (array != null) {
                    for (com.google.gson.JsonElement el : array) {
                        JsonObject obj = el.getAsJsonObject();
                        int id = obj.get("id").getAsInt();
                        int bookId = obj.get("book_id").getAsInt();
                        java.time.LocalDateTime ini = SupabaseClient.GSON.fromJson(obj.get("start_date"), java.time.LocalDateTime.class);
                        java.time.LocalDateTime fim = SupabaseClient.GSON.fromJson(obj.get("end_date"), java.time.LocalDateTime.class);
                        Book book = BookSystem.getById(bookId);
                        if (book != null)
                            student.addBookLoan(new org.example.entities.BookLoan(id, student, book, ini, fim, 0.0F));
                    }
                }
            }
        } else {
            // SQLite hydration
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans WHERE student_id=?")) {
                ps.setInt(1, student.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int bookId = rs.getInt("book_id");
                        java.time.LocalDateTime ini = SqliteCache.parseDate(rs.getString("start_date"));
                        java.time.LocalDateTime fim = SqliteCache.parseDate(rs.getString("end_date"));
                        Book book = BookSystem.getById(bookId);
                        if (book != null)
                            student.addBookLoan(new org.example.entities.BookLoan(id, student, book, ini, fim, 0.0F));
                    }
                }
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao hidratar empréstimos: " + e.getMessage());
            }
        }
    }

    private Student buildStudentFromRs(ResultSet rs) throws SQLException {
        Student s = new Student(rs.getInt("id"), rs.getString("name"), rs.getString("password"));
        s.setId(rs.getInt("id"));
        float penalty = rs.getFloat("pending_penalty");
        if (penalty > 0) s.addPendingPenalty(penalty);
        return s;
    }
}
