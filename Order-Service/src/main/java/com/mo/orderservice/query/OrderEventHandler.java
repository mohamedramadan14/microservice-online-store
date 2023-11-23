package com.mo.orderservice.query;

import com.mo.orderservice.core.OrderCreatedEvent;
import com.mo.orderservice.core.data.entity.OrderEntity;
import com.mo.orderservice.core.data.entity.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ProcessingGroup("order-group")
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderRepository repository;
    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(event, orderEntity);
        try {
            repository.save(orderEntity);
        }catch (Exception e) {
            log.info("Order Already Exists: {}", event.getOrderId());
        }
        log.info("Order Created: {}", event.getOrderId());
    }
}
