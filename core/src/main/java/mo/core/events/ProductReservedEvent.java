package mo.core.events;

import lombok.Data;

@Data
public class ProductReservedEvent {

    private String productId;
    private Integer qty;
    private String orderId;
    private String userId;
}
