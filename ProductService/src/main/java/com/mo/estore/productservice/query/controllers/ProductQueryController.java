package com.mo.estore.productservice.query.controllers;

import com.mo.estore.productservice.query.FindProductQuery;
import com.mo.estore.productservice.query.models.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductQueryController {

    QueryGateway queryGateway;

    @GetMapping
    public List<ProductResponse> getProducts() {
        FindProductQuery query = new FindProductQuery();
        return queryGateway.query(query ,
                ResponseTypes.multipleInstancesOf(ProductResponse.class)).join();
    }
}
