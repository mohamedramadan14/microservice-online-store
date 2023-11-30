package mo.mo.orderservice.core.events;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mo.mo.orderservice.command.models.OrderStatus;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private String productId;
    private Integer qty;
    private String addressId;
    private OrderStatus orderStatus;
}
