package com.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {


    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
