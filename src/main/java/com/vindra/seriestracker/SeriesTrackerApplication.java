package com.vindra.seriestracker;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import com.vindra.seriestracker.interceptor.KindleInterceptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(
        exclude = {SecurityAutoConfiguration.class}
)
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = {
        "com.amazon.kindle",
        "com.vindra.seriestracker",
        })
public class SeriesTrackerApplication {

    @Bean
    public KindleInterceptor myInterceptor() {
        return new KindleInterceptor();
    }


    public static void main(String[] args) {
        SpringApplication.run(SeriesTrackerApplication.class, args);
    }





}
