package com.kushi.in.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

/**
 * AWS Secrets Manager Configuration
 * Configures the AWS Secrets Manager client for retrieving application secrets
 */
@Configuration
public class AwsSecretsManagerConfig {

    @Value("${aws.region:ap-south-1}")
    private String awsRegion;

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}
