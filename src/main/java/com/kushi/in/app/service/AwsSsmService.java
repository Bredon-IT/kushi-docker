package com.kushi.in.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

@Service
public class AwsSsmService {

    @Autowired
    private SsmClient ssmClient;

    /**
     * Get parameter from AWS SSM Parameter Store
     * Results are cached to reduce AWS API calls
     */
    @Cacheable(value = "ssmParameters", key = "#parameterName")
    public String getParameter(String parameterName) {
        try {
            GetParameterRequest request = GetParameterRequest.builder()
                    .name(parameterName)
                    .withDecryption(true)
                    .build();

            GetParameterResponse response = ssmClient.getParameter(request);
            return response.parameter().value();
        } catch (SsmException e) {
            System.err.println("Error fetching SSM parameter: " + parameterName);
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get Google Analytics ID from SSM
     */
    public String getGoogleAnalyticsId() {
        String value = getParameter("/kushi/analytics/google-analytics-id");
        return value != null ? value : "G-XXXXXXXXXX";
    }

    /**
     * Get Facebook Pixel ID from SSM
     */
    public String getFacebookPixelId() {
        String value = getParameter("/kushi/analytics/facebook-pixel-id");
        return value != null ? value : "YOUR_PIXEL_ID";
    }
}
