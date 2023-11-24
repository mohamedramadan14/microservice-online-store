package mo.mo.estore.productservice.query;

import mo.core.events.ProductReservedEvent;
import mo.mo.estore.productservice.core.data.ProductEntity;
import mo.mo.estore.productservice.core.data.ProductRepository;
import mo.mo.estore.productservice.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public void on(ProductCreatedEvent event) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        try {
            repository.save(productEntity);
        } catch (IllegalArgumentException e) {
            log.info("Product Already Exists: {}", event.getProductId());
            e.printStackTrace();
        }
        log.info("Product Created: {}", productEntity.getProductId());
    }

    @EventHandler
    public void on(ProductReservedEvent event) {
        log.info("Product Reserved Event with ProductId: {}  start handling now ", event.getProductId());
        Optional<ProductEntity> product = repository.findById(event.getProductId());
        if (product.isPresent()) {
            ProductEntity entity = product.get();
            entity.setQty(entity.getQty() - event.getQty());
            repository.save(entity);
        }
    }
}
