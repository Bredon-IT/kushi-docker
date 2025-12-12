package com.kushi.in.app.controller;

import com.kushi.in.app.entity.Offer;
import com.kushi.in.app.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static com.kushi.in.app.config.AppConstants.*;
import static com.kushi.in.app.constants.KushiConstants.KUSHI_GLOBAL;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = { KUSHI_GLOBAL })
public class OfferController {

    @Autowired
    private OfferService offerService;

    // ================== GET ALL OFFERS ==================
    @GetMapping
    public List<Offer> getOffers() {
        return offerService.getAllOffers();
    }

    // ================== CREATE OFFER ==================
    @PostMapping
    public Offer createOffer(@RequestBody Offer offer) {
        return offerService.addOffer(offer);
    }

    // ================== UPDATE OFFER ==================
    @PutMapping("/{id}")
    public Offer updateOffer(@PathVariable Long id, @RequestBody Offer offer) {
        return offerService.updateOffer(id, offer);
    }

    // ================== DELETE OFFER ==================
    @DeleteMapping("/{id}")
    public void deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
    }

    // ================== IMAGE UPLOAD ==================
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = offerService.uploadAndGetPresigned(file);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}