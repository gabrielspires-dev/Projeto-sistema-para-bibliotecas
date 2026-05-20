-- Script de Criação de Tabelas no Supabase (PostgreSQL)
-- Copie e cole este script no SQL Editor do seu painel do Supabase

-- 1. Tabela de Estudantes (Students)
CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    pending_penalty DECIMAL(10,2) DEFAULT 0.00
);

-- 2. Tabela de Bibliotecários (Librarians)
CREATE TABLE IF NOT EXISTS librarians (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- 3. Tabela de Livros (Books)
CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

-- 4. Tabela de Empréstimos (Loans)
CREATE TABLE IF NOT EXISTS loans (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES students(id) ON DELETE CASCADE,
    book_id INTEGER REFERENCES books(id) ON DELETE CASCADE,
    start_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP NOT NULL,
    penalty DECIMAL(10,2) DEFAULT 0.00
);
