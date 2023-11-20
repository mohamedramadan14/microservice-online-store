package com.mo.estore.productservice.command;

import com.mo.estore.productservice.core.data.ProductLookupEntity;
import com.mo.estore.productservice.core.data.ProductLookupRepository;
import com.mo.estore.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
@RequiredArgsConstructor
@Slf4j
public class ProductLookupEventHandler {
    private final ProductLookupRepository repository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductLookupEntity productLookupEntity = new ProductLookupEntity();
        BeanUtils.copyProperties(event, productLookupEntity);
        repository.save(productLookupEntity);
        log.info("Product in Lookup Table of Product Command Created: {}", productLookupEntity.getProductId());
    }
}
