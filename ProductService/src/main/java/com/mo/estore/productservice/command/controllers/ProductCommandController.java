package com.mo.estore.productservice.command.controllers;

import com.mo.estore.productservice.command.CreateProductCommand;
import com.mo.estore.productservice.command.models.CreateProductModel;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductCommandController {

    private final  Environment env;
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductModel productModel){
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .price(productModel.getPrice())
                .qty(productModel.getQty())
                .title(productModel.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();
        String response = commandGateway.sendAndWait(createProductCommand);
//        try {
//            response = commandGateway.sendAndWait(createProductCommand);
//        } catch (Exception e) {
//            response = e.getLocalizedMessage();
//        }
        return "HTTP POST : Product Created: " + response;
    }

//    @GetMapping
//    public String getProduct(){
//        return "HTTP GET : Product Found";
//    }
//
//    @PutMapping
//    public String updateProduct(){
//        return "HTTP PUT : Product Updated";
//    }
//    @DeleteMapping
//    public String deleteProduct(){
//        return "HTTP DELETE : Product Deleted";
//    }
}
