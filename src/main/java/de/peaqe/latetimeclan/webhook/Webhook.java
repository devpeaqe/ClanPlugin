package de.peaqe.latetimeclan.webhook;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.03.2024 | 19:36 Uhr
 * *
 */

public class Webhook {

    private final String url;

    public Webhook() {
        this.url = "https://discord.com/api/webhooks/1219353374462447661/xS_3Cp4k_reu9Teaq_JB0deX8OValaBNsyOQteHrCc" +
                "FvuGp3Jy1Zy1ppB3iIxN2ycO34";
    }

    public void sendMessage(String message) {
        try {
            var webhookUrl = new URL(url);
            var connection = (HttpURLConnection) webhookUrl.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = "{\"content\": \"" + message + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                var input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            var responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);

            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmbed(
            String title,
            String fieldNameAction,
            String fieldNameValue,
            String color,
            String author,
            boolean timestamp
    ) {
        // Soon...
    }


}
