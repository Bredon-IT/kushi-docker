package com.kushi.in.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * AWS Secrets Manager Service
 * Retrieves and caches secrets from AWS Secrets Manager
 */
@Service
public class AwsSecretsManagerService {

    private static final Logger logger = LoggerFactory.getLogger(AwsSecretsManagerService.class);
    private static final String BACKEND_SECRET_NAME = "kushi/backend/credentials";
    private static final String FRONTEND_SECRET_NAME = "kushi/frontend/credentials";

    @Autowired
    private SecretsManagerClient secretsManagerClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, String> backendSecrets = new HashMap<>();
    private Map<String, String> frontendSecrets = new HashMap<>();

    /**
     * Initialize secrets on application startup
     */
    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing AWS Secrets Manager - Loading backend secrets...");
            backendSecrets = getSecretAsMap(BACKEND_SECRET_NAME);
            logger.info("✅ Backend secrets loaded successfully: {} keys found", backendSecrets.size());

            logger.info("Loading frontend secrets...");
            frontendSecrets = getSecretAsMap(FRONTEND_SECRET_NAME);
            logger.info("✅ Frontend secrets loaded successfully: {} keys found", frontendSecrets.size());
        } catch (Exception e) {
            logger.error("❌ Failed to load secrets from AWS Secrets Manager", e);
            throw new RuntimeException("Failed to initialize AWS Secrets Manager", e);
        }
    }

    /**
     * Get secret value as JSON map
     */
    @Cacheable(value = "awsSecrets", key = "#secretName")
    public Map<String, String> getSecretAsMap(String secretName) {
        try {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
            String secretString = response.secretString();

            // Parse JSON string to Map
            Map<String, String> secretMap = new HashMap<>();
            JsonNode jsonNode = objectMapper.readTree(secretString);
            jsonNode.fields().forEachRemaining(entry -> 
                secretMap.put(entry.getKey(), entry.getValue().asText())
            );

            logger.debug("Retrieved secret '{}' with {} keys", secretName, secretMap.size());
            return secretMap;
        } catch (Exception e) {
            logger.error("Failed to retrieve secret: {}", secretName, e);
            throw new RuntimeException("Failed to retrieve secret: " + secretName, e);
        }
    }

    /**
     * Get specific value from backend secrets
     */
    public String getBackendSecret(String key) {
        String value = backendSecrets.get(key);
        if (value == null) {
            logger.warn("Secret key '{}' not found in backend secrets", key);
        }
        return value;
    }

    /**
     * Get specific value from frontend secrets
     */
    public String getFrontendSecret(String key) {
        String value = frontendSecrets.get(key);
        if (value == null) {
            logger.warn("Secret key '{}' not found in frontend secrets", key);
        }
        return value;
    }

    // Convenience methods for commonly used secrets
    
    public String getRazorpayKeyId() {
        return getBackendSecret("RAZORPAY_KEY_ID");
    }

    public String getRazorpayKeySecret() {
        return getBackendSecret("RAZORPAY_KEY_SECRET");
    }

    public String getDatabaseUrl() {
        return getBackendSecret("DB_URL");
    }

    public String getDatabaseUsername() {
        return getBackendSecret("DB_USERNAME");
    }

    public String getDatabasePassword() {
        return getBackendSecret("DB_PASSWORD");
    }

    public String getAwsAccessKey() {
        return getBackendSecret("AWS_ACCESS_KEY");
    }

    public String getAwsSecretKey() {
        return getBackendSecret("AWS_SECRET_KEY");
    }

    public String getAwsRegion() {
        return getBackendSecret("AWS_REGION");
    }

    public String getEmailUsername() {
        return getBackendSecret("EMAIL_USERNAME");
    }

    public String getEmailPassword() {
        return getBackendSecret("EMAIL_PASSWORD");
    }

    /**
     * Refresh all secrets (call this if secrets are updated in AWS)
     */
    public void refreshSecrets() {
        logger.info("Refreshing all secrets from AWS Secrets Manager...");
        backendSecrets = getSecretAsMap(BACKEND_SECRET_NAME);
        frontendSecrets = getSecretAsMap(FRONTEND_SECRET_NAME);
        logger.info("✅ Secrets refreshed successfully");
    }
}
