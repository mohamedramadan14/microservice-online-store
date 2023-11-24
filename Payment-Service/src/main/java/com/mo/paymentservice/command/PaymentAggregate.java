package com.mo.paymentservice.command;

import lombok.NoArgsConstructor;
import mo.core.command.ProcessPaymentCommand;
import mo.core.events.PaymentProcessedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@NoArgsConstructor
@Aggregate
public class PaymentAggregate {
    @AggregateIdentifier
    private  String paymentId;
    private  String orderId;
    @CommandHandler
    public void handle(ProcessPaymentCommand command) {
        if(command.getPaymentDetails() == null){
            throw new IllegalArgumentException("Missing Payment Details");
        }
        if(command.getOrderId() == null){
            throw new IllegalArgumentException("Missing Order Id");
        }
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(command.getPaymentId())
                .orderId(command.getOrderId())
                .build();
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
    }
}
