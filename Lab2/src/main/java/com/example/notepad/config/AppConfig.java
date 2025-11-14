package com.example.notepad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Configuration
public class AppConfig {


    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    public static class RequestStamp {
        public final String id;
        public final Instant createdAt;
        public RequestStamp(String id, Instant createdAt) {
            this.id = id; this.createdAt = createdAt;
        }
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RequestStamp requestStamp(Clock clock) {
        return new RequestStamp(UUID.randomUUID().toString(), Instant.now(clock));
    }
}