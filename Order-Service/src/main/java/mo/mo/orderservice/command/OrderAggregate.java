package mo.mo.orderservice.command;

import mo.mo.orderservice.command.models.OrderStatus;
import mo.mo.orderservice.core.events.OrderApprovedEvent;
import mo.mo.orderservice.core.events.OrderCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class OrderAggregate {
    @AggregateIdentifier
    private  String orderId;
    private  String userId;
    private   String productId;
    private  Integer qty;
    private  String addressId;
    private OrderStatus orderStatus;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        if(createOrderCommand.getOrderId() == null || createOrderCommand.getOrderId().isBlank()){
            throw new IllegalArgumentException("OrderId cannot be empty");
        }
        if(createOrderCommand.getUserId() == null || createOrderCommand.getUserId().isBlank()){
            throw new IllegalArgumentException("UserId cannot be empty");
        }
        if(createOrderCommand.getProductId() == null || createOrderCommand.getProductId().isBlank()){
            throw new IllegalArgumentException("ProductId cannot be empty");
        }
        if(createOrderCommand.getQty() <= 0){
            throw new IllegalArgumentException("Quantity cannot be negative or equal to zero");
        }
        if(createOrderCommand.getAddressId() == null || createOrderCommand.getAddressId().isBlank()){
            throw new IllegalArgumentException("AddressId cannot be empty");
        }
        if(createOrderCommand.getOrderStatus() == null){
            throw new IllegalArgumentException("OrderStatus cannot be empty");
        }

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();

        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        if(approveOrderCommand.getOrderId() == null || approveOrderCommand.getOrderId().isBlank()){
            throw new IllegalArgumentException("OrderId cannot be empty");
        }
        // publish OrderApprovedEvent
        OrderApprovedEvent orderApprovedEvent = OrderApprovedEvent.builder()
                .orderId(approveOrderCommand.getOrderId())
                .orderStatus(OrderStatus.APPROVED)
                .build();
        AggregateLifecycle.apply(orderApprovedEvent);
    }
    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent){
        this.orderId = orderCreatedEvent.getOrderId();
        this.userId = orderCreatedEvent.getUserId();
        this.productId = orderCreatedEvent.getProductId();
        this.qty = orderCreatedEvent.getQty();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent){
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }
}
