package mo.mo.orderservice.core.events;

import lombok.Builder;
import lombok.Data;
import mo.mo.orderservice.command.models.OrderStatus;
@Data
@Builder
public class OrderApprovedEvent {
    private  final String orderId;
    private  final OrderStatus orderStatus;
}
