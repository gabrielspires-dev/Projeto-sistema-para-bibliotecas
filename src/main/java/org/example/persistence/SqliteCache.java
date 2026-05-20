package org.example.persistence;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SqliteCache — Persistência local offline via SQLite.
 * Funciona como fallback quando o Supabase não está configurado.
 * Cria e gerencia o arquivo "cache.db" na raiz do projeto.
 */
public class SqliteCache {

    private static final String DB_PATH = "cache.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static Connection connection;

    // ---- CONEXÃO ----

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite não encontrado: " + e.getMessage());
            }
            connection = DriverManager.getConnection(URL);
            connection.setAutoCommit(true);
        }
        return connection;
    }

    // ---- INICIALIZAÇÃO DO SCHEMA ----

    public static void init() {
        String createStudents = """
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                password TEXT NOT NULL,
                pending_penalty REAL DEFAULT 0.0
            )
            """;
        String createLibrarians = """
            CREATE TABLE IF NOT EXISTS librarians (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                password TEXT NOT NULL
            )
            """;
        String createBooks = """
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                is_available INTEGER DEFAULT 1
            )
            """;
        String createLoans = """
            CREATE TABLE IF NOT EXISTS loans (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                book_id INTEGER NOT NULL,
                start_date TEXT NOT NULL,
                end_date TEXT NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students(id),
                FOREIGN KEY (book_id) REFERENCES books(id)
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createStudents);
            stmt.execute(createLibrarians);
            stmt.execute(createBooks);
            stmt.execute(createLoans);
        } catch (SQLException e) {
            System.err.println("[cache.db] Erro ao inicializar schema: " + e.getMessage());
        }
    }

    // ---- HELPERS DE DATA ----

    public static String formatDate(LocalDateTime dt) {
        return dt == null ? null : dt.format(FMT);
    }

    public static LocalDateTime parseDate(String str) {
        if (str == null || str.isBlank()) return null;
        return LocalDateTime.parse(str, FMT);
    }
}
