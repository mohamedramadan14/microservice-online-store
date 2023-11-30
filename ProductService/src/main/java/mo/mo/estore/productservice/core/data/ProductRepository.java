package mo.mo.estore.productservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByProductId(String productId);
    ProductEntity findByTitle(String title);
    ProductEntity findByProductIdOrTitle(String productId, String title);

}
