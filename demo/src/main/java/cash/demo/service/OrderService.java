package cash.demo.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cash.demo.entity.Order;
import cash.demo.repository.OrderRepository;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;

    // create new order
    public Order createOrder(Order order){

        order.setOrderId("ORD-" +UUID.randomUUID().toString().substring(0,8).toUpperCase() );

        return orderRepository.save(order);

    }


    // Get order by orderId

   public Order getOrderByOrderId(String orderId){
       return orderRepository.findByOrderId(orderId)
       .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
   }


    // Update order staus.
    public Order updateOrderStatus(String orderId , String status){
          Order order=getOrderByOrderId(orderId);
          order.setStatus(status);

          return orderRepository.save(order);
    }

}
