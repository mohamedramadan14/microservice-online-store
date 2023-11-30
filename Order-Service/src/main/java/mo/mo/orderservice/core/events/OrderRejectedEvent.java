package mo.mo.orderservice.core.events;

import lombok.Builder;
import lombok.Data;
import mo.mo.orderservice.command.models.OrderStatus;

@Data
@Builder
public class OrderRejectedEvent {
    private final  String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
