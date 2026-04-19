package com.aztech.audit.service;

import com.aztech.audit.config.RabbitMqConfig;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ProductAuditListener {

    private static final Logger logger = LoggerFactory.getLogger(ProductAuditListener.class);

    @RabbitListener(queues = RabbitMqConfig.PRODUCT_AUDIT_QUEUE)
    public void listen(Map<String, Object> event) {
        logger.info(
                "Evento recebido no microsserviço de auditoria: ação={}, produto={}, categoria={}, data={}",
                event.get("action"),
                event.get("productName"),
                event.get("categoryName"),
                event.get("occurredAt")
        );
    }
}
