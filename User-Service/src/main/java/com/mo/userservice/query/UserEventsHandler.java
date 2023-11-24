package com.mo.userservice.query;

import mo.core.model.PaymentDetails;
import mo.core.model.User;
import mo.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import static java.lang.System.*;

@Component
public class UserEventsHandler {
    @QueryHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {
        out.println("HERE I AM : " + query);
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .name("John Doe")
                .cardNumber("1234567890123456")
                .validUntilMonth(12)
                .validUntilYear(2025)
                .cvv("123")
                .build();

        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();
    }

}
