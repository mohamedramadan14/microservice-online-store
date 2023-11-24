package com.mo.paymentservice.events;

import com.mo.paymentservice.data.PaymentEntity;
import com.mo.paymentservice.data.PaymentRepository;
import lombok.RequiredArgsConstructor;
import mo.core.events.PaymentProcessedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventsHandler {
    private final  PaymentRepository paymentRepository;
    private final Logger logger = LoggerFactory.getLogger(PaymentEventsHandler.class);

    @EventHandler
    public void handle(PaymentProcessedEvent event) {
        logger.info("PaymentProcessedEvent received with PaymentId: {} and OrderId: {}",
                event.getPaymentId(), event.getOrderId());
        PaymentEntity paymentEntity = new PaymentEntity();
        BeanUtils.copyProperties(event, paymentEntity);

        paymentRepository.save(paymentEntity);
        logger.info("PaymentProcessedEvent saved with PaymentId: {} and OrderId: {}",
                event.getPaymentId(), event.getOrderId());
    }

}
