
package com.htmb.bank.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class ApiClient {

    private static final String BASE_URL =
            "http://192.168.100.6:8080";

    public static String request(
            String method,
            String endpoint,
            String jsonBody,
            String token
    ) throws Exception {

        URL url = new URL(BASE_URL + endpoint);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty(
                "Content-Type", "application/json"
        );

        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty(
                    "Authorization", "Bearer " + token
            );
        }

        if (jsonBody != null) {
            connection.setDoOutput(true);

            try (OutputStream output =
                         connection.getOutputStream()) {

                output.write(
                        jsonBody.getBytes(StandardCharsets.UTF_8)
                );
            }
        }

        int statusCode = connection.getResponseCode();

        InputStream input = statusCode >= 400
                ? connection.getErrorStream()
                : connection.getInputStream();

        StringBuilder response = new StringBuilder();

        if (input != null) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        }

        connection.disconnect();

        return response.toString();
    }
}
