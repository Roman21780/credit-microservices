package com.example.creditapplicationservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue creditResponsesQueue() {
        return new Queue("credit-responses");
    }
}
