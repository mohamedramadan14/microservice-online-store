package mo.mo.orderservice.query;

import mo.mo.orderservice.core.events.OrderApprovedEvent;
import mo.mo.orderservice.core.events.OrderCreatedEvent;
import mo.mo.orderservice.core.data.entity.OrderEntity;
import mo.mo.orderservice.core.data.entity.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    @EventHandler
    public void on(OrderApprovedEvent event) {
        Optional<OrderEntity> order = repository.findByOrderId(event.getOrderId());
        if (order.isPresent()) {
            OrderEntity orderEntity =order.get();
            orderEntity.setOrderStatus(event.getOrderStatus());
            repository.save(orderEntity);
        }else{
            log.info("Order Not Found: {}", event.getOrderId());
        }
    }
}
