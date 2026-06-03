package org.example.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.*;
import java.time.LocalDateTime;
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
import org.example.system.StudentSystem;

public class BookLoanRepository implements BookLoanDAO {

    public void add(BookLoan loan) {
        if (DbConfig.isConfigured()) {
            JsonObject json = new JsonObject();
            json.addProperty("student_id", loan.getStudent().getId());
            json.addProperty("book_id", loan.getBook().getId());
            json.addProperty("start_date", loan.getInitialDate().toString());
            json.addProperty("end_date", loan.getFinalDate().toString());
            json.addProperty("penalty", 0.0);
            String response = SupabaseClient.post("loans", json.toString());
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                if (array != null && array.size() > 0)
                    loan.setId(array.get(0).getAsJsonObject().get("id").getAsInt());
            }
        } else {
            // SQLite
            String sql = "INSERT INTO loans (student_id, book_id, start_date, end_date) VALUES (?,?,?,?)";
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, loan.getStudent().getId());
                ps.setInt(2, loan.getBook().getId());
                ps.setString(3, SqliteCache.formatDate(loan.getInitialDate()));
                ps.setString(4, SqliteCache.formatDate(loan.getFinalDate()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) loan.setId(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao salvar empréstimo: " + e.getMessage());
            }
        }
    }

    public void remove(BookLoan loan) {
        if (DbConfig.isConfigured()) {
            SupabaseClient.delete("loans", "?id=eq." + loan.getId());
        } else {
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM loans WHERE id=?")) {
                ps.setInt(1, loan.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao remover empréstimo: " + e.getMessage());
            }
        }
    }

    public List<BookLoan> getAll() {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("loans", "?order=id.asc");
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                List<BookLoan> list = new ArrayList<>();
                if (array != null) {
                    for (JsonElement el : array) {
                        JsonObject obj = el.getAsJsonObject();
                        int id = obj.get("id").getAsInt();
                        int studentId = obj.get("student_id").getAsInt();
                        int bookId = obj.get("book_id").getAsInt();
                        LocalDateTime ini = SupabaseClient.GSON.fromJson(obj.get("start_date"), LocalDateTime.class);
                        LocalDateTime fim = SupabaseClient.GSON.fromJson(obj.get("end_date"), LocalDateTime.class);
                        Student student = StudentSystem.findById(studentId);
                        Book book = BookSystem.getById(bookId);
                        if (student != null && book != null)
                            list.add(new BookLoan(id, student, book, ini, fim, 0.0F));
                    }
                }
                return list;
            }
        }
        // SQLite
        List<BookLoan> list = new ArrayList<>();
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int studentId = rs.getInt("student_id");
                int bookId = rs.getInt("book_id");
                LocalDateTime ini = SqliteCache.parseDate(rs.getString("start_date"));
                LocalDateTime fim = SqliteCache.parseDate(rs.getString("end_date"));
                Student student = StudentSystem.findById(studentId);
                Book book = BookSystem.getById(bookId);
                if (student != null && book != null)
                    list.add(new BookLoan(id, student, book, ini, fim, 0.0F));
            }
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao listar empréstimos: " + e.getMessage());
        }
        return Collections.unmodifiableList(list);
    }
}
