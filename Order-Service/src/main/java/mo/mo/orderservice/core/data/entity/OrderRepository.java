package mo.mo.orderservice.core.data.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
   Optional<OrderEntity> findByOrderId(String orderId);
}
