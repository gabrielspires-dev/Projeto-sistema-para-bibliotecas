package org.example.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.entities.Book;
import org.example.persistence.DbConfig;
import org.example.persistence.SqliteCache;
import org.example.persistence.SupabaseClient;

public class BookRepository {

    public void addBook(Book book) {
        if (DbConfig.isConfigured()) {
            String checkResponse = SupabaseClient.get("books", "?id=eq." + book.getId());
            JsonArray array = checkResponse != null ? SupabaseClient.GSON.fromJson(checkResponse, JsonArray.class) : null;
            if (array != null && array.size() > 0) {
                JsonObject json = new JsonObject();
                json.addProperty("is_available", true);
                SupabaseClient.patch("books", "?id=eq." + book.getId(), json.toString());
            } else {
                JsonObject json = new JsonObject();
                json.addProperty("title", book.getName());
                json.addProperty("author", book.getAuthor());
                json.addProperty("is_available", true);
                String postResponse = SupabaseClient.post("books", json.toString());
                if (postResponse != null) {
                    JsonArray postArray = SupabaseClient.GSON.fromJson(postResponse, JsonArray.class);
                    if (postArray != null && postArray.size() > 0)
                        book.setId(postArray.get(0).getAsJsonObject().get("id").getAsInt());
                }
            }
        } else {
            // SQLite: verifica se o livro já existe (devolvido) ou é novo
            try (Connection conn = SqliteCache.getConnection()) {
                PreparedStatement check = conn.prepareStatement("SELECT id FROM books WHERE id=?");
                check.setInt(1, book.getId());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    // Livro devolvido: marca como disponível
                    PreparedStatement ps = conn.prepareStatement("UPDATE books SET is_available=1 WHERE id=?");
                    ps.setInt(1, book.getId());
                    ps.executeUpdate();
                } else {
                    // Novo livro
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO books (title, author, is_available) VALUES (?,?,1)",
                        Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, book.getName());
                    ps.setString(2, book.getAuthor());
                    ps.executeUpdate();
                    try (ResultSet gen = ps.getGeneratedKeys()) {
                        if (gen.next()) book.setId(gen.getInt(1));
                    }
                }
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao salvar livro: " + e.getMessage());
            }
        }
    }

    public Book removeBook(Book book) {
        if (DbConfig.isConfigured()) {
            JsonObject json = new JsonObject();
            json.addProperty("is_available", false);
            SupabaseClient.patch("books", "?id=eq." + book.getId(), json.toString());
        } else {
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE books SET is_available=0 WHERE id=?")) {
                ps.setInt(1, book.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao remover livro: " + e.getMessage());
            }
        }
        return book;
    }

    public boolean contains(Book book) {
        if (DbConfig.isConfigured()) {
            try {
                String et = java.net.URLEncoder.encode(book.getName(), "UTF-8");
                String ea = java.net.URLEncoder.encode(book.getAuthor(), "UTF-8");
                String response = SupabaseClient.get("books", "?title=eq." + et + "&author=eq." + ea + "&is_available=eq.true");
                JsonArray array = response != null ? SupabaseClient.GSON.fromJson(response, JsonArray.class) : null;
                return array != null && array.size() > 0;
            } catch (java.io.UnsupportedEncodingException e) {
                return false;
            }
        }
        // SQLite
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT id FROM books WHERE LOWER(title)=LOWER(?) AND LOWER(author)=LOWER(?) AND is_available=1")) {
            ps.setString(1, book.getName());
            ps.setString(2, book.getAuthor());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao verificar livro: " + e.getMessage());
            return false;
        }
    }

    public Book getById(int id) {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("books", "?id=eq." + id);
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                if (array != null && array.size() > 0)
                    return SupabaseClient.GSON.fromJson(array.get(0), Book.class);
            }
            return null;
        }
        // SQLite - busca livro seja disponível ou não (necessário para hidratar empréstimos)
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return buildBookFromRs(rs);
            }
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao buscar livro por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Book> getAll() {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("books", "?is_available=eq.true&order=id.asc");
            if (response != null) {
                Book[] arr = SupabaseClient.GSON.fromJson(response, Book[].class);
                List<Book> list = new ArrayList<>();
                if (arr != null) Collections.addAll(list, arr);
                return list;
            }
        }
        // SQLite
        List<Book> list = new ArrayList<>();
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE is_available=1 ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(buildBookFromRs(rs));
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao listar livros: " + e.getMessage());
        }
        return Collections.unmodifiableList(list);
    }

    private Book buildBookFromRs(ResultSet rs) throws SQLException {
        Book b = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
        return b;
    }
}