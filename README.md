# Sistema de Biblioteca

Sistema de gerenciamento de biblioteca executado via terminal, desenvolvido em Java. Suporta dois modos de persistência de dados: **online**, com banco de dados PostgreSQL hospedado no Supabase, e **offline**, com SQLite local (arquivo `cache.db`). A escolha do modo é feita automaticamente na inicialização, com base na presença do arquivo `supabase.properties`.

---

## Requisitos

| Dependência | Versão mínima |
|---|---|
| JDK | 18 |
| Maven | 3.8+ (opcional, apenas para testes via `mvn test`) |
| Supabase account | Somente para o modo online |

---

## Modos de Operação

### Modo offline (padrão)

Sem nenhuma configuração adicional, o sistema inicia em modo offline. Os dados são persistidos localmente no arquivo `cache.db` (SQLite), criado automaticamente na raiz do projeto na primeira execução.

### Modo online (Supabase)

Para ativar o modo online, execute o script de configuração e forneça as credenciais do seu projeto Supabase. Em seguida, aplique o schema do banco de dados.

```bash
bash cli/setup-supabase.sh
```

O script gera o arquivo `supabase.properties` na raiz do projeto:

```properties
supabase.url=https://<project-id>.supabase.co
supabase.key=<anon-public-key>
```

Após configurar as credenciais, crie as tabelas no SQL Editor do painel do Supabase usando o arquivo [`schema.sql`](schema.sql) incluído no repositório.

---

## Executando o Projeto

### Usando o script `run.sh` (recomendado)

O script compila todos os fontes e executa a aplicação em um único passo:

```bash
bash cli/run.sh
```

O script detecta automaticamente o modo ativo (online ou offline) e exibe uma mensagem informativa antes de iniciar.

### Compilação e execução manual

**Compilar:**

```bash
bash cli/compile.sh
```

**Executar (a partir da raiz do projeto):**

```bash
java -cp "target/classes:$HOME/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar:lib/sqlite-jdbc.jar:lib/slf4j-api.jar:lib/slf4j-nop.jar" org.example.Main
```

> A aplicação deve ser executada a partir da raiz do projeto para que o arquivo `supabase.properties` e o `cache.db` sejam encontrados corretamente.

### Usando Maven (somente para testes)

```bash
mvn test
```

---

## Dependências

As dependências de runtime estão incluídas no diretório `lib/`. As dependências de test são resolvidas pelo Maven.

| Artefato | Versão | Escopo |
|---|---|---|
| `gson` | 2.10.1 | runtime |
| `sqlite-jdbc` | bundled em `lib/` | runtime |
| `slf4j-api` / `slf4j-nop` | bundled em `lib/` | runtime |
| `junit-jupiter` | 5.10.2 | test |
| `mockito-core` | 5.11.0 | test |
| `mockito-junit-jupiter` | 5.11.0 | test |

---

## Arquitetura

O projeto é organizado em camadas bem definidas dentro do pacote `org.example`:

```
src/main/java/org/example/
|
+-- Main.java                    # Ponto de entrada. Inicializa o modo de persistência e abre o menu.
|
+-- entities/                   # Modelo de domínio
|   +-- SystemUser.java          # Interface comum a Student e Librarian
|   +-- Student.java             # Estudante; gerencia seus próprios empréstimos e multas
|   +-- Librarian.java           # Bibliotecário
|   +-- Book.java                # Livro do acervo
|   +-- BookLoan.java            # Empréstimo; calcula a multa dinamicamente por atraso
|
+-- auth/                       # Autenticação em sessão (estado em memória)
|   +-- StudentAuth.java
|   +-- LibrarianAuth.java
|
+-- system/                     # Lógica de negócio (fachada para cada entidade)
|   +-- BookSystem.java
|   +-- BookLoanSystem.java
|   +-- StudentSystem.java
|   +-- LibrarianSystem.java
|
+-- repositories/               # Camada de acesso a dados (padrão DAO)
|   +-- BookDAO.java             # Interface
|   +-- BookRepository.java      # Implementação (Supabase ou SQLite)
|   +-- BookLoanDAO.java
|   +-- BookLoanRepository.java
|   +-- StudentDAO.java
|   +-- StudentRepository.java
|   +-- LibrarianDAO.java
|   +-- LibrarianRepository.java
|
+-- persistence/                # Infraestrutura de persistência
|   +-- DbConfig.java            # Carrega e valida supabase.properties
|   +-- SupabaseClient.java      # Cliente HTTP para a API REST do Supabase (PostgREST)
|   +-- SqliteCache.java         # Gerencia a conexão SQLite e inicializa o schema offline
|   +-- PersistenceService.java  # Serializer JSON para o modo legado em arquivo
|
+-- menus/                      # Camada de apresentação (terminal interativo)
|   +-- TerminalMenu.java        # Menu genérico configurável por opções e ações (Runnable)
|   +-- TerminalMainMenu.java
|   +-- TerminalMenuStudentPage.java
|   +-- TerminalMenuLibrarianPage.java
|   +-- TerminalDecoration.java
|
+-- exceptions/                 # Exceções de domínio checadas
|   +-- BookNotFoundException.java
|   +-- BookNotAvailableException.java
|   +-- PendingPenaltyException.java
|
+-- utils/
    +-- TerminalUtils.java       # Scanner compartilhado, leitura segura de inteiros e datas
```

---

## Decisões de Design

### Dupla estratégia de persistência

Cada repositório (`BookRepository`, `StudentRepository`, etc.) implementa um padrão de seleção de backend em tempo de execução: se `DbConfig.isConfigured()` retornar `true`, opera contra a API REST do Supabase (PostgREST); caso contrário, usa JDBC diretamente contra o SQLite local. Essa condição é avaliada a cada chamada, tornando a troca de modo possível sem reinicialização de objetos.

### Cálculo de multa lazy

A multa de um empréstimo não é armazenada como um valor fixo no banco. O método `BookLoan.getPenalty()` calcula o valor no momento da chamada comparando `LocalDateTime.now()` com `finalDate`. A fórmula aplicada é:

```
multa = 0,00                                    se dentro do prazo
multa = 4,00 + (dias_de_atraso * 0,50)         caso contrário
```

### Livros emprestados e disponibilidade

Ao realizar um empréstimo, o livro é marcado como indisponível no banco (`is_available = false` / `0`) em vez de ser deletado. Isso preserva a integridade referencial dos empréstimos. Ao devolver, o livro é remarcado como disponível. No modo de arquivo JSON legado (`PersistenceService`), o livro emprestado é serializado dentro do próprio DTO do empréstimo para reconstrução posterior.

### IDs gerados pelo banco

Quando o Supabase está ativo, os objetos são criados com `id = 0`. Após o `INSERT`, o repositório aplica o `SERIAL` retornado pelo banco via `setId(dbId)`. No modo offline, o auto-increment do SQLite faz o trabalho ou um contador estático local (`idCount`) assume essa responsabilidade se necessário e é sincronizado ao carregar dados existentes.

### Injeção de dependência para testes

Cada classe `*System` expõe um método estático `setRepository(DAO repo)` que permite substituir o repositório real por um mock durante os testes. Isso viabiliza testes unitários isolados sem dependência de banco de dados.

---

## Funcionalidades

### Aluno

- Criar conta e fazer login por ID e senha
- Pegar um livro emprestado (prazo de 30 dias; bloqueado se houver multa pendente)
- Devolver um empréstimo (multa calculada automaticamente em caso de atraso)
- Listar empréstimos próprios com datas e multa atual
- Pagar multa pendente
- Editar nome e senha do perfil
- Deletar a própria conta (bloqueado se houver empréstimos em aberto)

### Bibliotecário

- Criar conta e fazer login por ID e senha
- Cadastrar, editar e remover livros do acervo
- Listar todos os empréstimos ativos (de todos os alunos)
- Listar todos os usuários registrados (alunos e bibliotecários)
- Editar o próprio perfil
- Deletar a própria conta

---

## Scripts Utilitários

Todos os scripts estão no diretório `cli/` e devem ser executados a partir da raiz do projeto ou via caminho relativo.

| Script | Descrição |
|---|---|
| `cli/run.sh` | Compila e executa a aplicação |
| `cli/compile.sh` | Apenas compila, verificando a presença do `javac` |
| `cli/setup-supabase.sh` | Configura o `supabase.properties` interativamente |
| `cli/reset-cache.sh` | Remove o `cache.db` após confirmação do usuário |

---

## Schema do Banco de Dados

O arquivo [`schema.sql`](schema.sql) contém as instruções DDL para o PostgreSQL (Supabase). O mesmo schema lógico é criado automaticamente para SQLite pelo `SqliteCache.init()` na inicialização offline.

```sql
students  (id, name, password, pending_penalty)
librarians(id, name, password)
books     (id, title, author, is_available)
loans     (id, student_id, book_id, start_date, end_date, penalty)
```

---

## Testes

Os testes unitários estão em `src/test/java/org/example/` e cobrem as classes de sistema e entidades:

- `StudentTest` / `StudentSystemTest`
- `BookSystemTest`
- `BookLoanTest` / `BookLoanSystemTest`
- `LibrarianSystemTest`

Os repositórios são mockados com Mockito via `setRepository()`. Para executar:

```bash
mvn test
```
