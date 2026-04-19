package com.aztech.productcms.service;

import com.aztech.productcms.config.RabbitMqConfig;
import com.aztech.productcms.dto.ProductEventDTO;
import com.aztech.productcms.model.Product;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ProductEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public ProductEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String action, Product product) {
        ProductEventDTO event = new ProductEventDTO(
                action,
                product.getId(),
                product.getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.PRODUCT_EXCHANGE,
                    "product." + action.toLowerCase(),
                    event
            );
            logger.info("Evento de produto publicado: {}", event);
        } catch (AmqpException exception) {
            logger.warn("RabbitMQ indisponível. Evento não publicado: {}", event, exception);
        }
    }
}
