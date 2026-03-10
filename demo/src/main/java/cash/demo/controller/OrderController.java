package cash.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cash.demo.entity.Order;
import cash.demo.service.CashfreeService;
import cash.demo.service.OrderService;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @Autowired 
    public CashfreeService cachfreeService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>>  createOrder(@RequestBody Order order){
        order.setId(null);
         Order createdOrder = orderService.createOrder(order);


        Map<String,Object> paymentSession= cachfreeService.createPaymentSession(

              createdOrder.getOrderId(),
              createdOrder.getAmount(),
              createdOrder.getCustomerName(),
              createdOrder.getCustomerEmail(),
              createdOrder.getCustomerPhone()
        );




         return ResponseEntity.ok(paymentSession);
    }



    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId){
        Order order = orderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(order);
    }
    
}
