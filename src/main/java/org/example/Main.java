package org.example;

import org.example.menus.TerminalMainMenu;
import org.example.persistence.DbConfig;
import org.example.persistence.SqliteCache;

public class Main {
    public static void main(String[] args) {
        // Inicializa o cache local SQLite (cache.db) quando Supabase não está configurado
        if (!DbConfig.isConfigured()) {
            SqliteCache.init();
            System.out.println("[cache.db] Modo offline ativo — dados persistidos em cache.db");
        } else {
            System.out.println("[Supabase] Modo online ativo — dados sincronizados na nuvem");
        }
        System.out.println();
        TerminalMainMenu.print();
    }
}