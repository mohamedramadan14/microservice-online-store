package mo.mo.estore.productservice.command;


import mo.core.command.CancelProductReservationCommand;
import mo.core.command.ReserveProductCommand;
import mo.core.events.ProductReservationCancelEvent;
import mo.core.events.ProductReservedEvent;
import mo.mo.estore.productservice.core.events.ProductCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private int qty;

    ProductAggregate(){
    }
    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Price cannot be less than or equal to zero");
        }
        if(createProductCommand.getQty() <= 0){
            throw new IllegalArgumentException("Quantity cannot be negative or equal to zero");
        }
        if(createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()){
            throw new IllegalArgumentException("Title cannot be empty");
        }
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();

        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        AggregateLifecycle.apply(productCreatedEvent);
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        this.productId = productCreatedEvent.getProductId();
        this.title = productCreatedEvent.getTitle();
        this.price = productCreatedEvent.getPrice();
        this.qty = productCreatedEvent.getQty();
    }
    @CommandHandler
    public ProductAggregate(ReserveProductCommand reserveProductCommand){
        if (qty < reserveProductCommand.getQty()){
            throw new IllegalArgumentException("Insufficient number of items in stock");
        }
        ProductReservedEvent productReservedEvent = new ProductReservedEvent();
        BeanUtils.copyProperties(reserveProductCommand, productReservedEvent);
        AggregateLifecycle.apply(productReservedEvent);
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent){
        this.qty -= productReservedEvent.getQty();
    }

    @CommandHandler
    public ProductAggregate (CancelProductReservationCommand cancelProductReservationCommand){
        ProductReservationCancelEvent productReservationCancelEvent = ProductReservationCancelEvent.builder()
                .orderId(cancelProductReservationCommand.getOrderId())
                .userId(cancelProductReservationCommand.getUserId())
                .productId(cancelProductReservationCommand.getProductId())
                .qty(cancelProductReservationCommand.getQty())
                .reason(cancelProductReservationCommand.getReason())
                .build();

        AggregateLifecycle.apply(productReservationCancelEvent);
    }

    @EventSourcingHandler
    public void on(ProductReservationCancelEvent productReservationCancelEvent){
        this.qty += productReservationCancelEvent.getQty();
    }
}
