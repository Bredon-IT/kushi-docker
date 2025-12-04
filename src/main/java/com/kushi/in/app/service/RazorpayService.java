package com.kushi.in.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id:rzp_live_RlYEJdBbbNn4Wq}")
    private String keyId;

    @Value("${razorpay.key.secret:jsQ0DZtrwxVZ8RWlrrZdlymJ}")
    private String keySecret;

    private static final String RAZORPAY_API_URL = "https://api.razorpay.com/v1";

    /**
     * Create Razorpay Order
     */
    public Map<String, Object> createOrder(Integer amount, String currency) throws Exception {
        if (currency == null || currency.isEmpty()) {
            currency = "INR";
        }

        // Create order JSON
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "rcpt_" + System.currentTimeMillis());

        // HTTP Client
        HttpClient client = HttpClient.newHttpClient();
        String auth = keyId + ":" + keySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RAZORPAY_API_URL + "/orders"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(orderRequest.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject orderResponse = new JSONObject(response.body());
            Map<String, Object> result = new HashMap<>();
            result.put("id", orderResponse.getString("id"));
            result.put("entity", orderResponse.getString("entity"));
            result.put("amount", orderResponse.getInt("amount"));
            result.put("currency", orderResponse.getString("currency"));
            result.put("receipt", orderResponse.getString("receipt"));
            result.put("status", orderResponse.getString("status"));
            return result;
        } else {
            throw new Exception("Razorpay order creation failed: " + response.body());
        }
    }

    /**
     * Verify Payment Signature
     */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature = calculateHMAC(payload, keySecret);
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify Webhook Signature
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            String webhookSecret = keySecret; // Use separate webhook secret in production
            String generatedSignature = calculateHMAC(payload, webhookSecret);
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get Payment Details
     */
    public Map<String, Object> getPaymentDetails(String paymentId) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String auth = keyId + ":" + keySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RAZORPAY_API_URL + "/payments/" + paymentId))
                .header("Authorization", "Basic " + encodedAuth)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject paymentResponse = new JSONObject(response.body());
            Map<String, Object> result = new HashMap<>();
            result.put("id", paymentResponse.getString("id"));
            result.put("entity", paymentResponse.getString("entity"));
            result.put("amount", paymentResponse.getInt("amount"));
            result.put("currency", paymentResponse.getString("currency"));
            result.put("status", paymentResponse.getString("status"));
            result.put("method", paymentResponse.optString("method", ""));
            result.put("email", paymentResponse.optString("email", ""));
            result.put("contact", paymentResponse.optString("contact", ""));
            return result;
        } else {
            throw new Exception("Failed to fetch payment details: " + response.body());
        }
    }

    /**
     * Initiate Refund
     */
    public Map<String, Object> initiateRefund(String paymentId, Integer amount) throws Exception {
        JSONObject refundRequest = new JSONObject();
        if (amount != null) {
            refundRequest.put("amount", amount); // Partial refund
        }
        // If amount is null, full refund will be processed

        HttpClient client = HttpClient.newHttpClient();
        String auth = keyId + ":" + keySecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RAZORPAY_API_URL + "/payments/" + paymentId + "/refund"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(refundRequest.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject refundResponse = new JSONObject(response.body());
            Map<String, Object> result = new HashMap<>();
            result.put("id", refundResponse.getString("id"));
            result.put("entity", refundResponse.getString("entity"));
            result.put("amount", refundResponse.getInt("amount"));
            result.put("payment_id", refundResponse.getString("payment_id"));
            result.put("status", refundResponse.getString("status"));
            return result;
        } else {
            throw new Exception("Refund failed: " + response.body());
        }
    }

    /**
     * Calculate HMAC SHA256 Signature
     */
    private String calculateHMAC(String data, String secret) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hash = sha256HMAC.doFinal(data.getBytes());
        return bytesToHex(hash);
    }

    /**
     * Convert bytes to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
