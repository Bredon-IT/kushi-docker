package com.kushi.in.app.controller;

import com.kushi.in.app.model.PaymentOrderRequest;
import com.kushi.in.app.model.PaymentVerificationRequest;
import com.kushi.in.app.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"https://kushiservices.com","https://www.kushiservices.com"})
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    /**
     * Create Razorpay Order
     * Endpoint: POST /api/payment/create-order
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentOrderRequest request) {
        try {
            Map<String, Object> orderData = razorpayService.createOrder(
                    request.getAmount(),
                    request.getCurrency()
            );
            return ResponseEntity.ok(orderData);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Verify Payment Signature
     * Endpoint: POST /api/payment/verify-signature
     */
    @PostMapping("/verify-signature")
    public ResponseEntity<?> verifySignature(@RequestBody PaymentVerificationRequest request) {
        try {
            boolean isValid = razorpayService.verifyPaymentSignature(
                    request.getRazorpay_order_id(),
                    request.getRazorpay_payment_id(),
                    request.getRazorpay_signature()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("verified", isValid);
            response.put("message", isValid ? "Payment verified successfully" : "Invalid signature");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get Payment Details
     * Endpoint: GET /api/payment/details/{paymentId}
     */
    @GetMapping("/details/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String paymentId) {
        try {
            Map<String, Object> paymentDetails = razorpayService.getPaymentDetails(paymentId);
            return ResponseEntity.ok(paymentDetails);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch payment details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Initiate Refund
     * Endpoint: POST /api/payment/refund
     */
    @PostMapping("/refund")
    public ResponseEntity<?> initiateRefund(@RequestBody Map<String, Object> request) {
        try {
            String paymentId = (String) request.get("payment_id");
            Integer amount = (Integer) request.get("amount"); // Amount in paise (optional for partial refund)

            Map<String, Object> refundData = razorpayService.initiateRefund(paymentId, amount);
            return ResponseEntity.ok(refundData);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Refund failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Webhook Handler (for Razorpay payment events)
     * Endpoint: POST /api/payment/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            boolean isValid = razorpayService.verifyWebhookSignature(payload, signature);

            if (isValid) {
                // Process the webhook event (payment.captured, payment.failed, etc.)
                // Add your business logic here
                System.out.println("Webhook verified: " + payload);
                return ResponseEntity.ok("Webhook processed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }
}
