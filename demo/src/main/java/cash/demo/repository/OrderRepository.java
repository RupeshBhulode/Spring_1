package cash.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cash.demo.entity.Order;
import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order,Long> {

    // Built in - save(order) , findById(id) , findAll() , delete(List<Order> findByOrderId(String orderId);
    // Explicityl created - findByOrderId

    // optional - hnadles null safty

    Optional<Order> findByOrderId(String orderId);
    
}
