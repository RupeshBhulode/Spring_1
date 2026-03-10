package cash.demo.controller;

import cash.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private OrderService orderService;

    @Value("${cashfree.secret-key}")
    private String secretKey;

    @PostMapping("/cashfree")
    public ResponseEntity<String> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("x-webhook-signature") String signature,
            @RequestHeader("x-webhook-timestamp") String timestamp) {

        try {
            // Step 1: Verify signature
            boolean isValid = verifySignature(payload.toString(), timestamp, signature);
            
            if (!isValid) {
                return ResponseEntity.status(401).body("Invalid signature");
            }

            // Step 2: Get order details from payload
            Map<String, Object> orderData = (Map<String, Object>) payload.get("data");
            Map<String, Object> orderInfo = (Map<String, Object>) orderData.get("order");
            Map<String, Object> paymentInfo = (Map<String, Object>) orderData.get("payment");

            String orderId = (String) orderInfo.get("order_id");
            String paymentStatus = (String) paymentInfo.get("payment_status");

            // Step 3: Update order status in DB
            if (paymentStatus.equals("SUCCESS")) {
                orderService.updateOrderStatus(orderId, "SUCCESS");
            } else if (paymentStatus.equals("FAILED")) {
                orderService.updateOrderStatus(orderId, "FAILED");
            }

            return ResponseEntity.ok("Webhook processed!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Verify that webhook is actually from Cashfree
    private boolean verifySignature(String payload, String timestamp, String signature) {
        try {
            String data = timestamp + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            String computedSignature = Base64.getEncoder().encodeToString(hash);
            return computedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}