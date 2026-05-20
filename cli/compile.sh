#!/bin/bash
# ==========================================================
#  compile.sh — Compila o Sistema de Biblioteca
# ==========================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

GSON="$HOME/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
SQLITE="$PROJECT_DIR/lib/sqlite-jdbc.jar"
SLF4J_API="$PROJECT_DIR/lib/slf4j-api.jar"
SLF4J_NOP="$PROJECT_DIR/lib/slf4j-nop.jar"
OUT="$PROJECT_DIR/target/classes"

echo "========================================"
echo "  Sistema de Biblioteca — Compilação"
echo "========================================"

# Verifica Java
if ! command -v javac &> /dev/null; then
    echo "[ERRO] Java (javac) não encontrado. Instale o JDK 17+."
    exit 1
fi

mkdir -p "$OUT"

SOURCES=$(find "$PROJECT_DIR/src" -name "*.java")
COUNT=$(echo "$SOURCES" | wc -l)
echo "Compilando $COUNT arquivos .java..."

javac -cp "$GSON:$SQLITE:$SLF4J_API:$SLF4J_NOP" -d "$OUT" $SOURCES 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "[OK] Compilação concluída com sucesso!"
    echo "     Classes geradas em: target/classes/"
else
    echo ""
    echo "[ERRO] Compilação falhou. Verifique os erros acima."
    exit 1
fi
