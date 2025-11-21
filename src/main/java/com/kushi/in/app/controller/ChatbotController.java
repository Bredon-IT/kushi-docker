package com.kushi.in.app.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final RestTemplate restTemplate;

    public ChatbotController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/query", produces = "application/json")   // ⭐ Force JSON output
    public ResponseEntity<Map<String, Object>> forwardChat(@RequestBody Map<String, String> body) {

        String userMessage = body.get("message");

        String pythonUrl = "https://main.dhtawzq4yzgjo.amplifyapp.com/chat";

        // JSON headers for Python
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = new HashMap<>();
        payload.put("message", userMessage);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        // Send request to Python
        ResponseEntity<Map> response =
                restTemplate.postForEntity(pythonUrl, requestEntity, Map.class);

        // ⭐ Force JSON response
        Map<String, Object> json = new HashMap<>();
        json.put("reply", response.getBody().get("reply"));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)  // ⭐ Force JSON
                .body(json);
    }
}
