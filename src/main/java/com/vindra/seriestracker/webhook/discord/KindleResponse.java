package com.vindra.seriestracker.webhook.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.amazon.kindle.WebPage;
import com.vindra.seriestracker.entity.Kindle;
import org.hibernate.type.Type;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.swing.text.Document;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class KindleResponse {

    // use tor or not
    @Value("${jsoup.tor:true}")
    private boolean tor;


    public void onKindleUpdate(Serializable asin, Object[] currentState, Object[] previousState, String[] propertyNames) {

        // to get the index of kindle field name inside the propertyNames array
        List<String> propertyList = Arrays.asList(propertyNames);
        String title = currentState[propertyList.indexOf("title")].toString();

        //generate which field is updated and what is the new status
        Map<String, String> fields = ResponseFactory.generateKindleUpdateMap(currentState, previousState, propertyNames);

        if (fields.size() == 0) {
            return;
        }

        // populate embed with title , description and url
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(String.format("[UPDATE] %s", title), "https://www.amazon.co.jp/dp/" + asin.toString()))
                .setDescription("Update on Kindle details")
                .setImageUrl(getImageFromAsin(asin.toString()))
                .setFooter(new WebhookEmbed.EmbedFooter("Powered by Spring Boot", "https://pbs.twimg.com/profile_images/1235868806079057921/fTL08u_H_400x400.png"));

        // populate the fields
        addFields(embedBuilder, fields);



        // create massage to combine embed and role mention
        WebhookMessageBuilder message =
                new WebhookMessageBuilder().
                        addEmbeds(embedBuilder.build()).
                        append("\\<@&866935403424120853>");

        // trigger webhook
        DiscordWebhook.generateWebhook().send(message.build());


//        webhookClient.send("test update");

        System.out.println("post update to webhook");
    }

    public void onKindleSeriesCollectionUpdate(Kindle kindle) {

        // populate embed with kindle fields
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(String.format("[NEW RELEASE] %s", kindle.getTitle()), "https://www.amazon.co.jp/dp/" + kindle.getAsin()))
                .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "ASIN", kindle.getAsin()))
                    .addField(new WebhookEmbed.EmbedField(false, "Price", Integer.toString(kindle.getPrice()) + "¥"))
                    .addField(new WebhookEmbed.EmbedField(false, "Pre Order", (kindle.isPreOrder() ? "Yes" : "No")))
                    .addField(new WebhookEmbed.EmbedField(false, "Kindle Unlimited Eligible", (kindle.isKindleUnlimited() ? "Yes" : "No")))
                    .addField(new WebhookEmbed.EmbedField(false, "Publisher", kindle.getPublisher()))
                    .addField(new WebhookEmbed.EmbedField(false, "Release Date", kindle.getPublicationDate().toString()))
                    .addField(new WebhookEmbed.EmbedField(false, "Kobo Url", String.format("https://www.kobo.com/jp/en/ebook/%s", kindle.getKoboId())))
                .setImageUrl(getImageFromAsin(kindle.getAsin()))
                .setFooter(new WebhookEmbed.EmbedFooter("Powered by Spring Boot", "https://pbs.twimg.com/profile_images/1235868806079057921/fTL08u_H_400x400.png"));

        // create massage to combine embed and role mention
        WebhookMessageBuilder message =
                new WebhookMessageBuilder().
                        addEmbeds(embedBuilder.build()).
                        append("\\<@&866935403424120853>");


        // trigger webhook
        DiscordWebhook.generateWebhook().send(message.build());


    }

    public void onNewKindleSeries(String seriesAsin, Object[] state, String[] propertyNames) {
        // create map from object for easier access to fields
        Map<String, String> seriesMap = ResponseFactory.generateKindleSeriesMap(state, propertyNames);


        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(String.format("[TRACK SERIES] %s", seriesMap.get("seriesTitle")), "https://www.amazon.co.jp/dp/" + seriesAsin))
                .setDescription("")
                .addField(new WebhookEmbed.EmbedField(true, "Number of Books", seriesMap.get("numberOfBook")))
                .setImageUrl(getImageFromSeriesAsin(seriesAsin))
                .setFooter(new WebhookEmbed.EmbedFooter("Powered by Spring Boot", "https://pbs.twimg.com/profile_images/1235868806079057921/fTL08u_H_400x400.png"));

        // create massage to combine embed and role mention
        WebhookMessageBuilder message =
                new WebhookMessageBuilder().
                        addEmbeds(embedBuilder.build()).
                        append("\\<@&866935403424120853>");


        DiscordWebhook.generateWebhook().send(message.build());

    }

    public String getImageFromAsin(String asin) {
        // retrieve web page
        Element page;

        if (tor){
            page = WebPage.getWebPageWithProxy("https://www.amazon.co.jp/dp/" + asin);
        } else {
            page = WebPage.getWebPage("https://www.amazon.co.jp/dp/" + asin);
        }


        // grab image url
        String imageUrl = page.getElementById("ebooks-img-canvas")
                .getElementsByTag("img").last()
                .attr("src");

        return imageUrl;
    }

    public String getImageFromSeriesAsin(String seriesAsin) {
//        seriesImageBlock
        // retrieve web page
        Element page;

        if (tor){
            page = WebPage.getWebPageWithProxy("https://www.amazon.co.jp/dp/" + seriesAsin);
        } else {
            page = WebPage.getWebPage("https://www.amazon.co.jp/dp/" + seriesAsin);
        }


        // grab image url
        String imageUrl = page.getElementById("seriesImageBlock")
                .attr("src");

        return imageUrl;
    }

    public void addFields(WebhookEmbedBuilder embedBuilder, Map<String, String> map) {
        map.forEach((name, value) -> {
            embedBuilder.addField(new WebhookEmbed.EmbedField(false, name, value));
        });

    }

}
