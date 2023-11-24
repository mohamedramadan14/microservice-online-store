package mo.core.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentProcessedEvent {
    private final String paymentId;
    private final String orderId;
}
