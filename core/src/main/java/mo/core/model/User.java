package mo.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
private String firstName;
private String lastName;
private String userId;
private PaymentDetails paymentDetails;
}
