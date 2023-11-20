package com.mo.estore.productservice.query;

import com.mo.estore.productservice.core.data.ProductEntity;
import com.mo.estore.productservice.core.data.ProductRepository;
import com.mo.estore.productservice.query.models.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductQueryHandler {
    private final ProductRepository productRepository;

    @QueryHandler
    public List<ProductResponse> findProducts(FindProductQuery query) {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(productEntity -> {
            ProductResponse productResponse = new ProductResponse();
            BeanUtils.copyProperties(productEntity, productResponse);
            return productResponse;
        }).toList();
    }

}

