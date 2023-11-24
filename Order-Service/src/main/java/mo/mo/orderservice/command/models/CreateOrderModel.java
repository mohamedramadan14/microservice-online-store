package mo.mo.orderservice.command.models;

import lombok.Data;

@Data
public class CreateOrderModel {
    private String productId;
    private Integer qty;
    private String addressId;
}
