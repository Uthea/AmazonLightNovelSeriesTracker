package com.vindra.seriestracker.webhook.discord;

import club.minnced.discord.webhook.WebhookClient;

public class DiscordWebhook {

    private final static String url = "INSERT_DISCORD_WEBHOOK_URL";

    public static WebhookClient generateWebhook() {

        return WebhookClient.withUrl(url);

    }

}
