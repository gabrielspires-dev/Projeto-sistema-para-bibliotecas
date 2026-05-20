#!/bin/bash
# ==========================================================
#  setup-supabase.sh — Configura as credenciais do Supabase
# ==========================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
PROPS="$PROJECT_DIR/supabase.properties"

echo "========================================"
echo "  Configuração do Supabase"
echo "========================================"
echo ""
echo "Você encontra as credenciais em:"
echo "  Supabase Dashboard → Settings → API"
echo ""

read -p "URL do projeto (ex: https://xxxx.supabase.co): " URL
read -p "Chave anon/public: " KEY

if [[ -z "$URL" || -z "$KEY" ]]; then
    echo "[ERRO] URL e Chave são obrigatórios."
    exit 1
fi

cat > "$PROPS" <<EOF
supabase.url=$URL
supabase.key=$KEY
EOF

echo ""
echo "[OK] Configuração salva em supabase.properties"
echo "     Execute ./cli/run.sh para iniciar o sistema."
echo ""
echo "Lembre-se de criar as tabelas no Supabase!"
echo "  → Use o arquivo schema.sql no SQL Editor do painel."
