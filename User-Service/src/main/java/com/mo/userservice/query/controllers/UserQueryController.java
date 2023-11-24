package com.mo.userservice.query.controllers;

import lombok.RequiredArgsConstructor;
import mo.core.model.User;
import mo.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserQueryController {

    QueryGateway queryGateway;
    @GetMapping("/{userId}/payment-details")
    public User getUserPaymentDetails(@PathVariable String userId) {
        FetchUserPaymentDetailsQuery query = new FetchUserPaymentDetailsQuery(userId);
        return queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();
    }
}
