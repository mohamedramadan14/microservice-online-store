package mo.mo.orderservice.command.controllers;

import mo.mo.orderservice.command.CreateOrderCommand;
import mo.mo.orderservice.command.models.CreateOrderModel;
import mo.mo.orderservice.command.models.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderCommandController {
    private final CommandGateway commonGateway;
    @PostMapping
    public String createOrder(@RequestBody CreateOrderModel orderModel){
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(UUID.randomUUID().toString())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .productId(orderModel.getProductId())
                .qty(orderModel.getQty())
                .addressId(orderModel.getAddressId())
                .orderStatus(OrderStatus.CREATED)
                .build();

        String response = commonGateway.sendAndWait(createOrderCommand);

        return "HTTP POST : Order Created" + response;
    }
}
