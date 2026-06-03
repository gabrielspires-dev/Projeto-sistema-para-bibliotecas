package org.example.repositories;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.entities.Librarian;
import org.example.persistence.DbConfig;
import org.example.persistence.SqliteCache;
import org.example.persistence.SupabaseClient;

public class LibrarianRepository implements LibrarianDAO {

    public void add(Librarian librarian) {
        if (DbConfig.isConfigured()) {
            JsonObject json = new JsonObject();
            json.addProperty("name", librarian.getName());
            json.addProperty("password", librarian.getPassword());
            String response = SupabaseClient.post("librarians", json.toString());
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                if (array != null && array.size() > 0)
                    librarian.setId(array.get(0).getAsJsonObject().get("id").getAsInt());
            }
        } else {
            // SQLite
            String sql = "INSERT INTO librarians (name, password) VALUES (?,?)";
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, librarian.getName());
                ps.setString(2, librarian.getPassword());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) librarian.setId(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao salvar bibliotecário: " + e.getMessage());
            }
        }
    }

    public void removeById(int id) {
        if (DbConfig.isConfigured()) {
            SupabaseClient.delete("librarians", "?id=eq." + id);
        } else {
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM librarians WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao remover bibliotecário: " + e.getMessage());
            }
        }
    }

    public Librarian getById(int id) {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("librarians", "?id=eq." + id);
            if (response != null) {
                JsonArray array = SupabaseClient.GSON.fromJson(response, JsonArray.class);
                if (array != null && array.size() > 0)
                    return SupabaseClient.GSON.fromJson(array.get(0), Librarian.class);
            }
            return null;
        }
        // SQLite
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM librarians WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return buildLibrarianFromRs(rs);
            }
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao buscar bibliotecário: " + e.getMessage());
        }
        return null;
    }

    public List<Librarian> getAll() {
        if (DbConfig.isConfigured()) {
            String response = SupabaseClient.get("librarians", "?order=id.asc");
            if (response != null) {
                Librarian[] arr = SupabaseClient.GSON.fromJson(response, Librarian[].class);
                List<Librarian> list = new ArrayList<>();
                if (arr != null) Collections.addAll(list, arr);
                return list;
            }
        }
        // SQLite
        List<Librarian> list = new ArrayList<>();
        try (Connection conn = SqliteCache.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM librarians ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(buildLibrarianFromRs(rs));
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao listar bibliotecários: " + e.getMessage());
        }
        return Collections.unmodifiableList(list);
    }

    private Librarian buildLibrarianFromRs(ResultSet rs) throws SQLException {
        Librarian l = new Librarian(rs.getInt("id"), rs.getString("name"), rs.getString("password"));
        return l;
    }

    @Override
    public void update(Librarian librarian) {
        if (DbConfig.isConfigured()) {
            JsonObject json = new JsonObject();
            json.addProperty("name", librarian.getName());
            json.addProperty("password", librarian.getPassword());
            SupabaseClient.patch("librarians", "?id=eq." + librarian.getId(), json.toString());
        } else {
            String sql = "UPDATE librarians SET name=?, password=? WHERE id=?";
            try (Connection conn = SqliteCache.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, librarian.getName());
                ps.setString(2, librarian.getPassword());
                ps.setInt(3, librarian.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("[cache.db] Erro ao atualizar bibliotecário: " + e.getMessage());
            }
        }
    }
}