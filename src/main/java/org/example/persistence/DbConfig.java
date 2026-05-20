package org.example.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DbConfig {
    private static final String FILE_PATH = "supabase.properties";
    private static String url = "";
    private static String key = "";
    private static boolean isConfigured = false;

    static {
        Properties properties = new Properties();
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
                url = properties.getProperty("supabase.url", "").trim();
                key = properties.getProperty("supabase.key", "").trim();

                if (!url.isEmpty() && !url.contains("seu-projeto-id") && !key.isEmpty() && !key.contains("sua-chave-anon")) {
                    isConfigured = true;
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar o arquivo de configuração do Supabase: " + e.getMessage());
            }
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getKey() {
        return key;
    }

    public static boolean isConfigured() {
        return isConfigured;
    }
}
