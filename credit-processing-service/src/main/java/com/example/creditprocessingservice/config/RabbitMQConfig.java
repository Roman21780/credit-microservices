package com.example.creditprocessingservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.credit-responses}")
    private String queueName;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Bean
    public DirectExchange creditExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue creditResponsesQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding binding(Queue creditResponsesQueue, DirectExchange creditExchange) {
        return BindingBuilder.bind(creditResponsesQueue)
                .to(creditExchange)
                .with(queueName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setConfirmCallback((correlation, ack, reason) -> {
            if (!ack) {
                log.error("Message failed to reach queue: {}", reason);
            } else {
                log.debug("Message confirmed for queue: credit-responses");
            }
        });
        template.setMandatory(true);
        template.setChannelTransacted(true);
        return template;
    }
}