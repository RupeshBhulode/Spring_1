package cash.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CashfreeService {
    
    @Value("${cashfree.app-id}")
    private String appId;

    @Value("${cashfree.secret-key}")
    private String secretKey;

    @Value("${cashfree.api-url}")
    private String apiUrl;

    @Value("${cashfree.api-version}")
    private String apiVersion;

    public Map<String, Object> createPaymentSession(String orderId, 
                                                     Double amount, 
                                                     String customerName, 
                                                     String customerEmail, 
                                                     String customerPhone,
                                                     String baseUrl) {

        RestTemplate restTemplate = new RestTemplate();

        // Setting headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-version", apiVersion);
        headers.set("x-client-id", appId);
        headers.set("x-client-secret", secretKey);

        // Setting Order Details
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order_id", orderId);
        requestBody.put("order_amount", amount);
        requestBody.put("order_currency", "INR");

        // Customer Details
        Map<String, String> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", "CUST-" + customerPhone);
        customerDetails.put("customer_name", customerName);
        customerDetails.put("customer_email", customerEmail);
        customerDetails.put("customer_phone", customerPhone);
        requestBody.put("customer_details", customerDetails);

        // Order Meta - dynamic webhook and return URL
        Map<String, String> orderMeta = new HashMap<>();
        orderMeta.put("notify_url", baseUrl + "/api/webhook/cashfree");
        orderMeta.put("return_url", baseUrl + "/api/orders/{order_id}");
        requestBody.put("order_meta", orderMeta);

        // HttpEntity - combines headers + body together
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Call Cashfree API
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

        return response.getBody();
    }
}
