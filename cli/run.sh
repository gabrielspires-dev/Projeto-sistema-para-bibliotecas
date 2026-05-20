#!/bin/bash
# ==========================================================
#  run.sh — Compila e executa o Sistema de Biblioteca
# ==========================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

GSON="$HOME/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
SQLITE="$PROJECT_DIR/lib/sqlite-jdbc.jar"
SLF4J_API="$PROJECT_DIR/lib/slf4j-api.jar"
SLF4J_NOP="$PROJECT_DIR/lib/slf4j-nop.jar"
OUT="$PROJECT_DIR/target/classes"
PROPS="$PROJECT_DIR/supabase.properties"

# ---- Compila ----
echo "========================================"
echo "  Sistema de Biblioteca — Iniciando..."
echo "========================================"
echo ""

mkdir -p "$OUT"
SOURCES=$(find "$PROJECT_DIR/src" -name "*.java")
javac -cp "$GSON:$SQLITE:$SLF4J_API:$SLF4J_NOP" -d "$OUT" $SOURCES 2>&1

if [ $? -ne 0 ]; then
    echo "[ERRO] Compilação falhou. Verifique os erros acima."
    exit 1
fi

# ---- Detecta modo (Supabase ou cache.db local) ----
if grep -q "seu-projeto-id" "$PROPS" 2>/dev/null || ! grep -q "supabase.url" "$PROPS" 2>/dev/null; then
    echo "Modo: OFFLINE (cache.db local — SQLite)"
else
    echo "Modo: ONLINE (Supabase — PostgreSQL na nuvem)"
fi
echo "========================================"
echo ""

# ---- Executa (roda na raiz do projeto para achar supabase.properties e cache.db) ----
cd "$PROJECT_DIR"
java -cp "$OUT:$GSON:$SQLITE:$SLF4J_API:$SLF4J_NOP" org.example.Main
