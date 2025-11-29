package com.kushi.in.app.controller;

import com.kushi.in.app.entity.Offer;
import com.kushi.in.app.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.kushi.in.app.config.AppConstants.*;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = {"https://kushiservices.com","https://www.kushiservices.com"}) // {KUSHI_SERVICES_URL, KUSHI_SERVICES_WWW_URL})
public class OfferController {

    @Autowired
    private OfferService offerService;

    // ================== OFFERS ==================
    @GetMapping
    public List<Offer> getOffers() {
        return offerService.getAllOffers();
    }

    @PostMapping
    public Offer createOffer(@RequestBody Offer offer) {
        return offerService.addOffer(offer);
    }

    @PutMapping("/{id}")
    public Offer updateOffer(@PathVariable Long id, @RequestBody Offer offer) {
        return offerService.updateOffer(id, offer);
    }

    @DeleteMapping("/{id}")
    public void deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
    }

    // ================== BANNERS ==================
    @GetMapping("/banners")
    public List<Offer> getBanners() {
        return offerService.getAllBanners();
    }

    @PostMapping("/banners")
    public Offer createBanner(@RequestBody Offer banner) {
        return offerService.addBanner(banner);
    }

    @PutMapping("/banners/{id}")
    public Offer updateBanner(@PathVariable Long id, @RequestBody Offer banner) {
        return offerService.updateBanner(id, banner);
    }

    @DeleteMapping("/banners/{id}")
    public void deleteBanner(@PathVariable Long id) {
        offerService.deleteBanner(id);
    }

    // ================== IMAGE UPLOAD ==================
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty!");

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);

            // Correct URL
            String fileUrl = "https://main.dhtawzq4yzgjo.amplifyapp.com/uploads/" + fileName;

            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    // ================== IMAGE UPLOAD (banners) ==================
    @PostMapping("/banners/upload")
    public ResponseEntity<?> uploadBannerImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty!");

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);

            // Correct URL
            String fileUrl = "https://main.dhtawzq4yzgjo.amplifyapp.com/uploads/" + fileName;

            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}

