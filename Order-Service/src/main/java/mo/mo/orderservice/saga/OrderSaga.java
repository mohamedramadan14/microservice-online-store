package mo.mo.orderservice.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mo.core.command.ProcessPaymentCommand;
import mo.core.command.ReserveProductCommand;
import mo.core.events.ProductReservedEvent;
import mo.core.model.User;
import mo.core.query.FetchUserPaymentDetailsQuery;
import mo.mo.orderservice.core.events.OrderCreatedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
@RequiredArgsConstructor
@Slf4j
public class OrderSaga {

    private final  CommandGateway commandGateway;

    private final  QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .qty(orderCreatedEvent.getQty())
                .orderId(orderCreatedEvent.getOrderId())
                .userId(orderCreatedEvent.getUserId())
                .build();

        log.info("OrderCreatedEvent received with OrderId: {} and ProductId: {}"
                , reserveProductCommand.getOrderId()
                , reserveProductCommand.getProductId());
        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // start compensating Saga transaction
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // process user payment
        log.info("ProductReservedEvent received with OrderId: {} and ProductId: {}"
                , productReservedEvent.getOrderId()
                , productReservedEvent.getProductId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway
                    .query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception e) {
            // TODO: handle exception compensating Saga transaction
            log.error("Error while fetching user payment details : " + e.getMessage());
        }

        if (userPaymentDetails == null) {
            // start compensating Saga transaction
            log.info("User Payment Details : {}  not found", userPaymentDetails);
            return;
        }
        log.info("User Payment Details : {}  fetched successfully", userPaymentDetails);


        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .build();

        String processPaymentCommandResult = null;
        try {
            processPaymentCommandResult = commandGateway.sendAndWait(processPaymentCommand , 10 ,  TimeUnit.SECONDS);
        } catch (Exception e) {
            // TODO: handle exception compensating Saga transaction
            log.error("Error while processing payment : " + e.getMessage());
            // start compensating Saga transaction
        }
        if(processPaymentCommandResult == null) {
            log.info("Payment failed processing : {}", processPaymentCommandResult);
            //start compensating Saga transaction
        }
    }
}
