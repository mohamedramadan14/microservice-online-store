package com.mo.estore.productservice.query;

import com.mo.estore.productservice.core.data.ProductEntity;
import com.mo.estore.productservice.core.data.ProductRepository;
import com.mo.estore.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ProcessingGroup("product-group")
public class ProductEventHandler {
    private final ProductRepository repository;

    @ExceptionHandler(resultType = Exception.class)
    public void handleOtherExceptions(Exception exception) throws Exception {
        log.info("Other Exception: {}", exception.getMessage());
        throw exception;
    }
    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handleIllegalArgumentExceptions(IllegalArgumentException exception) throws IllegalArgumentException {
        log.info("IllegalArgumentException: {}", exception.getMessage());
        throw exception;
    }
    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        try {
            repository.save(productEntity);
        } catch (Exception e) {
            log.info("Product Already Exists: {}", event.getProductId());
        }
        //repository.save(productEntity);
        log.info("Product Created: {}", productEntity.getProductId());
        throw new Exception("Something went wrong in the command handler");
    }
}
