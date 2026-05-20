package org.example.persistence;

import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SupabaseClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static boolean warnedOnce = false;
    private static final DateTimeFormatter FMT_ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter FMT_DB = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, t, ctx) ->
                    new JsonPrimitive(src.format(FMT_ISO)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, t, ctx) -> {
                String str = json.getAsString();
                // O PostgreSQL/Supabase pode retornar com milissegundos ou fuso horário. Ajustamos para ler o prefixo ISO.
                if (str.contains("Z")) str = str.replace("Z", "");
                if (str.contains(" ")) str = str.replace(" ", "T");
                if (str.contains(".")) str = str.split("\\.")[0];
                try {
                    return LocalDateTime.parse(str, FMT_ISO);
                } catch (DateTimeParseException e) {
                    return LocalDateTime.parse(str, FMT_DB);
                }
            })
            .create();

    public static String sendRequest(String method, String table, String queryParams, String body) {
        if (!DbConfig.isConfigured()) {
            if (!warnedOnce) {
                System.out.println("\n[Aviso] Supabase não está configurado em 'supabase.properties'. Rodando em modo inativo (somente memória).");
                warnedOnce = true;
            }
            return null;
        }

        try {
            String urlStr = DbConfig.getUrl() + "/rest/v1/" + table;
            if (queryParams != null && !queryParams.isEmpty()) {
                urlStr += queryParams;
            }

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .header("apikey", DbConfig.getKey())
                    .header("Authorization", "Bearer " + DbConfig.getKey())
                    .header("Content-Type", "application/json");

            if ("POST".equalsIgnoreCase(method)) {
                builder.POST(HttpRequest.BodyPublishers.ofString(body))
                       .header("Prefer", "return=representation");
            } else if ("PATCH".equalsIgnoreCase(method)) {
                builder.method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                       .header("Prefer", "return=representation");
            } else if ("DELETE".equalsIgnoreCase(method)) {
                builder.DELETE();
            } else {
                builder.GET();
            }

            HttpRequest request = builder.build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                return response.body();
            } else {
                System.err.println("Erro na requisição Supabase (" + method + " " + table + "): HTTP " + status);
                System.err.println("Resposta: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Falha na comunicação com o Supabase: " + e.getMessage());
        }
        return null;
    }

    public static String get(String table, String queryParams) {
        return sendRequest("GET", table, queryParams, null);
    }

    public static String post(String table, String json) {
        return sendRequest("POST", table, null, json);
    }

    public static String patch(String table, String queryParams, String json) {
        return sendRequest("PATCH", table, queryParams, json);
    }

    public static void delete(String table, String queryParams) {
        sendRequest("DELETE", table, queryParams, null);
    }
}
