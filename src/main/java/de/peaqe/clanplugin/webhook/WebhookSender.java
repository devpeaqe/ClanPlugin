package de.peaqe.clanplugin.webhook;

import java.io.IOException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 19.03.2024 | 10:35 Uhr
 * *
 */

public class WebhookSender {

    public void sendWebhook(DiscordWebhook.EmbedObject... embedObjects) {
        try {

            var webhook = new DiscordWebhook();

            for (var embedObject : embedObjects) {
                webhook.addEmbed(embedObject);
            }

            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
