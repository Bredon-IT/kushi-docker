package com.kushi.in.app.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class GoogleReviewController {

    private static final String API_KEY = "AIzaSyDxgwMmVq2T-VC8CVQyRSXu_aAU1UAuSqs";
    private static final String PLACE_ID = "ChIJ3S9FM6MTrjsRnW-EP3Rwa8Q";

    @GetMapping("/reviews")
    public ResponseEntity<Object> getReviews() {
        try {
            String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id="
                    + PLACE_ID + "&fields=reviews,rating,user_ratings_total,url&key=" + API_KEY;

            RestTemplate restTemplate = new RestTemplate();
            Object response = restTemplate.getForObject(url, Object.class);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching Google reviews: " + e.getMessage());
        }
    }
}
