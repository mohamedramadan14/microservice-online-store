package mo.mo.orderservice.saga;

import mo.core.command.CancelProductReservationCommand;
import mo.core.command.ProcessPaymentCommand;
import mo.core.command.ReserveProductCommand;
import mo.core.events.PaymentProcessedEvent;
import mo.core.events.ProductReservationCancelEvent;
import mo.core.events.ProductReservedEvent;
import mo.core.model.User;
import mo.core.query.FetchUserPaymentDetailsQuery;
import mo.mo.orderservice.command.ApproveOrderCommand;
import mo.mo.orderservice.command.RejectOrderCommand;
import mo.mo.orderservice.core.events.OrderApprovedEvent;
import mo.mo.orderservice.core.events.OrderCreatedEvent;
import mo.mo.orderservice.core.events.OrderRejectedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
public class OrderSaga {
    @Autowired
    private transient  CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;
    private final Logger logger = LoggerFactory.getLogger(OrderSaga.class);

    private void cancelProductReservation(ProductReservedEvent event, String reason) {
        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .productId(event.getProductId())
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .qty(event.getQty())
                .reason(reason)
                .build();
        commandGateway.send(cancelProductReservationCommand);
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .qty(orderCreatedEvent.getQty())
                .orderId(orderCreatedEvent.getOrderId())
                .userId(orderCreatedEvent.getUserId())
                .build();

        logger.info("OrderCreatedEvent handled for orderId: {} and productId: {}" ,
                reserveProductCommand.getOrderId() ,reserveProductCommand.getProductId());
        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if(commandResultMessage.isExceptional()) {
                // Start a compensating transaction
                RejectOrderCommand rejectOrderCommand = RejectOrderCommand .builder()
                        .orderId(orderCreatedEvent.getOrderId())
                        .reason(commandResultMessage.exceptionResult().getMessage())
                        .build();

                commandGateway.send(rejectOrderCommand);
            }

        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // process user payment
        logger.info("ProductReservedEvent is called for productId: {} and orderId: {}"
                ,productReservedEvent.getProductId()
                ,productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway
                    .query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception e) {
            // start compensating Saga transaction
            logger.error("Error while fetching user payment details : {}" , e.getMessage());
            cancelProductReservation(productReservedEvent , e.getMessage());
            return;
        }

        if (userPaymentDetails == null) {
            // start compensating Saga transaction
            logger.info("User Payment Details : {}  not found", userPaymentDetails);
            cancelProductReservation(productReservedEvent , "Payment Details not found");
            return;
        }
        logger.info("User Payment Details : {}  fetched successfully", userPaymentDetails);


        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .build();

        String processPaymentCommandResult = null;
        try {
            processPaymentCommandResult = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception e) {
            logger.error("Error while processing payment : {} " , e.getMessage());
            // start compensating Saga transaction
            cancelProductReservation(productReservedEvent , e.getMessage());
            return;
        }
        if(processPaymentCommandResult == null) {
            logger.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");
            //start compensating Saga transaction
            cancelProductReservation(productReservedEvent  , "Couldn't process payment with these details");
        }
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        logger.info("PaymentProcessedEvent finished successfully received with PaymentId in SAGA: {} and OrderId: {}"
                , paymentProcessedEvent.getPaymentId()
                , paymentProcessedEvent.getOrderId());
        // Send ApproveOrderCommand
        ApproveOrderCommand approveOrderCommand = ApproveOrderCommand.builder()
                .orderId(paymentProcessedEvent.getOrderId())
                .build();
        commandGateway.send(approveOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        logger.info("OrderApprovedEvent received with OrderId: {}", orderApprovedEvent.getOrderId());
        logger.info("Order is approved. Order Saga is complete for orderId: {}" , orderApprovedEvent.getOrderId());
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelEvent productReservationCancelEvent){
            // send rejectOrderCommand
        RejectOrderCommand rejectOrderCommand = RejectOrderCommand.builder()
                .orderId(productReservationCancelEvent.getOrderId())
                .reason(productReservationCancelEvent.getReason())
                .build();

        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent){
        logger.info("Successfully rejected order with Id: {}",orderRejectedEvent.getOrderId());
    }

}
