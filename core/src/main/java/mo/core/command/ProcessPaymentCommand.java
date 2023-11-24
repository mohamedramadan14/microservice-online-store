package mo.core.command;

import lombok.Builder;
import lombok.Data;
import mo.core.model.PaymentDetails;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Data
@Builder
public class ProcessPaymentCommand {
    @TargetAggregateIdentifier
    private final String paymentId;
    private final String orderId;
    private final PaymentDetails paymentDetails;
}
