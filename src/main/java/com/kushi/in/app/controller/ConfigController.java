package com.kushi.in.app.controller;

import com.kushi.in.app.service.AwsSsmService;
import com.kushi.in.app.service.AwsSecretsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigController {

    @Autowired
    private AwsSsmService awsSsmService;

    @Autowired(required = false)
    private AwsSecretsManagerService secretsManagerService;

    @Value("${razorpay.key.id:rzp_test_XXXXXXXXXXXX}")
    private String razorpayKeyId;

    /**
     * Get analytics configuration from AWS SSM
     * Frontend calls this on app load to get tracking IDs
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, String>> getAnalyticsConfig() {
        Map<String, String> config = new HashMap<>();
        
        config.put("googleAnalyticsId", awsSsmService.getGoogleAnalyticsId());
        config.put("facebookPixelId", awsSsmService.getFacebookPixelId());
        
        return ResponseEntity.ok(config);
    }

    /**
     * Get Razorpay public key (KEY_ID)
     * ⚠️ Only exposes PUBLIC key - SECRET key stays in backend
     * Frontend needs this to initialize Razorpay checkout
     */
    @GetMapping("/razorpay")
    public ResponseEntity<Map<String, String>> getRazorpayConfig() {
        Map<String, String> config = new HashMap<>();
        
        // Only expose public KEY_ID (safe to expose)
        config.put("keyId", razorpayKeyId);
        
        return ResponseEntity.ok(config);
    }

    /**
     * Health check for configuration endpoints
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "Configuration API is healthy");
        
        // Check if secrets are loaded
        if (secretsManagerService != null) {
            String dbUrl = secretsManagerService.getDatabaseUrl();
            status.put("secretsLoaded", dbUrl != null ? "true" : "false");
        }
        
        return ResponseEntity.ok(status);
    }
}
