package mo.mo.orderservice.core.events;

import mo.mo.orderservice.command.models.OrderStatus;
import lombok.Data;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private String productId;
    private Integer qty;
    private String addressId;
    private OrderStatus orderStatus;
}
