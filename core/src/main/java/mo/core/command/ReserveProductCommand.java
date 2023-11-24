package mo.core.command;


import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class ReserveProductCommand {

    @TargetAggregateIdentifier
    private String productId;
    private Integer qty;
    private String orderId;
    private String userId;

}
