package cash.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cash.demo.entity.Order;
import cash.demo.service.CashfreeService;
import cash.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @Autowired 
    private CashfreeService cashfreeService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Order order,
            HttpServletRequest servletRequest) {

        order.setId(null);

        // Dynamically get base URL from incoming request
        String baseUrl = servletRequest.getScheme() + "://" + 
                         servletRequest.getServerName();

        Order createdOrder = orderService.createOrder(order);

        Map<String, Object> paymentSession = cashfreeService.createPaymentSession(
            createdOrder.getOrderId(),
            createdOrder.getAmount(),
            createdOrder.getCustomerName(),
            createdOrder.getCustomerEmail(),
            createdOrder.getCustomerPhone(),
            baseUrl
        );

        return ResponseEntity.ok(paymentSession);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = orderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(order);
    }
}
