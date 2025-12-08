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
@CrossOrigin(origins = {"https://dev.dhtawzq4yzgjo.amplifyapp.com"}) // {KUSHI_SERVICES_URL, KUSHI_SERVICES_WWW_URL})
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
        return handleFileUpload(file);
    }

    // ================== SHARED UPLOAD HANDLER ==================
    private ResponseEntity<?> handleFileUpload(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File is empty"));
            }

            String original = file.getOriginalFilename();

            // Extract name without last extension
            String base = (original == null)
                    ? "file"
                    : original.replaceAll("\\.[^.]+$", "");

            // Extract correct extension
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : ".png";

            String safeName = base + ext;

            // Save inside project/uploads folder
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // Unique output filename
            String fileName = System.currentTimeMillis() + "_" + safeName;
            File destFile = new File(dir, fileName);

            // Write file to disk
            file.transferTo(destFile);

            // Build public URL
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}