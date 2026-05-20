#!/bin/bash
# ==========================================================
#  reset-cache.sh — Apaga o cache.db local (dados offline)
# ==========================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
DB="$PROJECT_DIR/cache.db"

echo "========================================"
echo "  Reset do cache local (cache.db)"
echo "========================================"
echo ""

if [ ! -f "$DB" ]; then
    echo "Nenhum cache.db encontrado. Nada a fazer."
    exit 0
fi

read -p "Tem certeza que deseja apagar TODOS os dados locais? (s/N): " CONFIRM
if [[ "$CONFIRM" == "s" || "$CONFIRM" == "S" ]]; then
    rm "$DB"
    echo "[OK] cache.db removido. Na próxima execução, um banco limpo será criado."
else
    echo "Operação cancelada."
fi
