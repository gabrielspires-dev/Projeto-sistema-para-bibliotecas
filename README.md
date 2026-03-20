# 📚 Sistema Bibliotecário

Sistema de gerenciamento de biblioteca desenvolvido em Java como trabalho acadêmico da disciplina de **Programação Orientada a Objetos**. Permite o cadastro de livros, gestão de alunos, controle de empréstimos e aplicação de multas por atraso.

---

## ✨ Funcionalidades

- **Autenticação** — Login e cadastro separados para Alunos e Bibliotecários, com senha e ID único
- **Gestão de Livros** — Cadastro e remoção de livros pelo bibliotecário
- **Empréstimos** — Retirada e devolução de livros com prazo de 30 dias
- **Multas automáticas** — Cálculo dinâmico de multa por atraso (R$ 4,00 fixo + R$ 0,50/dia)
- **Pagamento de multas** — Aluno quita pendências antes de realizar novo empréstimo
- **Histórico de empréstimos** — Consulta detalhada por aluno
- **Persistência de dados** — Estado do sistema salvo em JSON via Gson (livros, alunos, bibliotecários, empréstimos)
- **Tratamento de exceções** — Livro inexistente, indisponível ou multa pendente são tratados com mensagens claras

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas inspirada nos princípios de POO:

```
src/main/java/org/example/
│
├── entities/          # Entidades de domínio (Book, Student, Librarian, BookLoan)
├── repositories/      # Acesso e armazenamento em memória dos dados
├── system/            # Regras de negócio (BookSystem, StudentSystem, BookLoanSystem...)
├── auth/              # Autenticação de sessão (StudentAuth, LibrarianAuth)
├── menus/             # Interface de terminal (menus e decoração visual)
├── exceptions/        # Exceções de domínio customizadas
├── persistence/       # Serialização/desserialização JSON com Gson
└── Main.java          # Ponto de entrada
```

---

## 🔄 Fluxo principal

```
Início
  └── Menu Principal
        ├── Área do Aluno
        │     ├── Criar conta / Login
        │     ├── Ver livros disponíveis e pegar emprestado
        │     ├── Devolver livro
        │     ├── Listar empréstimos e multas
        │     └── Pagar multa pendente
        └── Área do Bibliotecário
              ├── Criar conta / Login
              ├── Cadastrar livro
              ├── Listar e remover livros
              └── Listar todos os empréstimos
```

---

## ⚙️ Pré-requisitos

- Java **18+**
- Maven **3.8+**

---

## 🚀 Como executar

**1. Clone o repositório**
```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

**2. Compile o projeto**
```bash
mvn compile
```

**3. Execute**
```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

> Os dados são salvos automaticamente na pasta `data/` na raiz do projeto.

---

## 🧪 Exceções tratadas

| Situação | Exceção |
|---|---|
| Livro não encontrado no sistema | `BookNotFoundException` |
| Livro já emprestado por outro aluno | `BookNotAvailableException` |
| Aluno com multa pendente tentando novo empréstimo | `PendingPenaltyException` |

---

## 🛠️ Tecnologias utilizadas

| Tecnologia | Uso |
|---|---|
| Java 18 | Linguagem principal |
| Maven | Gerenciamento de dependências e build |
| [Gson 2.10.1](https://github.com/google/gson) | Serialização e persistência em JSON |

---

## 📁 Estrutura de dados persistidos

Após a primeira execução, a pasta `data/` conterá:

```
data/
├── books.json        # Livros disponíveis na biblioteca
├── students.json     # Cadastro de alunos
├── librarians.json   # Cadastro de bibliotecários
└── loans.json        # Empréstimos ativos (com o livro embutido no JSON)
```

---

## 👥 Autores

Desenvolvido para a disciplina de **Programação Orientada a Objetos**
Docente: Gabriela de Matos Cezar

---

## 📄 Licença

Este projeto é de uso acadêmico.
