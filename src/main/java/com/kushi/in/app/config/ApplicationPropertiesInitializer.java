package com.kushi.in.app.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Application Properties Initializer
 * Loads secrets from AWS Secrets Manager BEFORE Spring Boot starts
 * This ensures all @Value annotations can resolve secrets from the environment
 */
public class ApplicationPropertiesInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationPropertiesInitializer.class);
    private static final String BACKEND_SECRET_NAME = "kushi/backend/credentials";
    private static final String AWS_REGION = "ap-south-1";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        logger.info("🔐 Loading secrets from AWS Secrets Manager: {}", BACKEND_SECRET_NAME);
        
        try {
            Map<String, Object> secrets = loadSecretsFromAWS();
            
            if (secrets.isEmpty()) {
                logger.error("❌ No secrets loaded from AWS Secrets Manager!");
                throw new RuntimeException("Failed to load secrets from AWS Secrets Manager");
            }

            // Add secrets to Spring Environment
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            environment.getPropertySources().addFirst(new MapPropertySource("awsSecrets", secrets));
            
            logger.info("✅ Successfully loaded {} secrets from AWS Secrets Manager", secrets.size());
            logger.debug("Loaded secret keys: {}", secrets.keySet());
            
        } catch (Exception e) {
            logger.error("❌ Failed to load secrets from AWS Secrets Manager", e);
            throw new RuntimeException("Failed to initialize application with AWS secrets", e);
        }
    }

    private Map<String, Object> loadSecretsFromAWS() {
        Map<String, Object> properties = new HashMap<>();
        
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(AWS_REGION))
                .build()) {
            
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(BACKEND_SECRET_NAME)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            String secretString = response.secretString();

            // Parse JSON and map to property names expected by application.properties
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(secretString);
            
            // Map AWS Secrets Manager keys to application.properties keys
            mapSecret(properties, jsonNode, "RAZORPAY_KEY_ID", "razorpay.key.id");
            mapSecret(properties, jsonNode, "RAZORPAY_KEY_SECRET", "razorpay.key.secret");
            mapSecret(properties, jsonNode, "DB_URL_dev", "spring.datasource.url");
            mapSecret(properties, jsonNode, "DB_USERNAME", "spring.datasource.username");
            mapSecret(properties, jsonNode, "DB_PASSWORD", "spring.datasource.password");
            mapSecret(properties, jsonNode, "AWS_ACCESS_KEY", "cloud.aws.credentials.access-key");
            mapSecret(properties, jsonNode, "AWS_SECRET_KEY", "cloud.aws.credentials.secret-key");
            mapSecret(properties, jsonNode, "AWS_REGION", "cloud.aws.region.static");
            mapSecret(properties, jsonNode, "EMAIL_USERNAME", "spring.mail.username");
            mapSecret(properties, jsonNode, "EMAIL_PASSWORD", "spring.mail.password");
            mapSecret(properties, jsonNode, "SES_FROM_EMAIL", "aws.ses.from-email");
            
            logger.info("📝 Mapped {} secret values to application properties", properties.size());
            
        } catch (Exception e) {
            logger.error("Error loading secrets from AWS", e);
            throw new RuntimeException("Failed to load secrets", e);
        }
        
        return properties;
    }

    private void mapSecret(Map<String, Object> properties, JsonNode jsonNode, String secretKey, String propertyName) {
        if (jsonNode.has(secretKey)) {
            String value = jsonNode.get(secretKey).asText();
            properties.put(propertyName, value);
            logger.debug("✓ Mapped {} -> {}", secretKey, propertyName);
        } else {
            logger.warn("⚠️ Secret key '{}' not found in AWS Secrets Manager", secretKey);
        }
    }
}
