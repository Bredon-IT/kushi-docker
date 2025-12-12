package com.kushi.in.app.service;

import com.kushi.in.app.dao.OfferRepository;
import com.kushi.in.app.entity.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private software.amazon.awssdk.services.s3.S3Client s3Client;
    @Autowired
    private software.amazon.awssdk.services.s3.presigner.S3Presigner s3Presigner;

    @org.springframework.beans.factory.annotation.Value("${aws.s3.bucketName}")
    private String bucketName;

    // ================== GET ALL OFFERS ==================
    public List<Offer> getAllOffers() {
        List<Offer> offers = offerRepository.findAll();
        for (Offer o : offers) {
            String img = o.getImageUrl();
            if (img != null && !img.trim().isEmpty() && !img.startsWith("http") && !img.startsWith("/uploads/")) {
                // It's an S3 Key
                o.setImageUrl(getPresignedUrl(img));
            }
        }
        return offers;
    }

    // ================== ADD OFFER ==================
    public Offer addOffer(Offer offer) {

        if (offer.getText() == null)
            offer.setText("");
        if (offer.getFontFamily() == null)
            offer.setFontFamily("Arial");
        if (offer.getColor() == null)
            offer.setColor("#000000");
        if (offer.getEmoji() == null)
            offer.setEmoji("");

        // Clean URL if it's a Presigned URL (extract key)
        if (offer.getImageUrl() != null) {
            offer.setImageUrl(extractKeyFromUrl(offer.getImageUrl()));
        } else {
            offer.setImageUrl("");
        }

        return offerRepository.save(offer);
    }

    // ================== UPDATE OFFER ==================
    public Offer updateOffer(Long id, Offer updatedOffer) {

        Optional<Offer> optional = offerRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Offer not found with id: " + id);
        }

        Offer offer = optional.get();

        offer.setText(updatedOffer.getText() != null ? updatedOffer.getText() : "");
        offer.setFontFamily(updatedOffer.getFontFamily() != null ? updatedOffer.getFontFamily() : "Arial");
        offer.setColor(updatedOffer.getColor() != null ? updatedOffer.getColor() : "#000000");
        offer.setEmoji(updatedOffer.getEmoji() != null ? updatedOffer.getEmoji() : "");

        // Only update image if user sent a new one
        if (updatedOffer.getImageUrl() != null && !updatedOffer.getImageUrl().trim().isEmpty()) {
            offer.setImageUrl(extractKeyFromUrl(updatedOffer.getImageUrl()));
        }

        return offerRepository.save(offer);
    }

    // ================== DELETE OFFER ==================
    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    // ================== S3 HELPERS ==================

    public String uploadAndGetPresigned(org.springframework.web.multipart.MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = java.util.UUID.randomUUID().toString() + extension;

            software.amazon.awssdk.services.s3.model.PutObjectRequest putOb = software.amazon.awssdk.services.s3.model.PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(putOb, software.amazon.awssdk.core.sync.RequestBody
                    .fromInputStream(file.getInputStream(), file.getSize()));

            return getPresignedUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }

    private String getPresignedUrl(String key) {
        if (key == null || key.isEmpty())
            return null;
        try {
            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest presignRequest = software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
                    .builder()
                    .signatureDuration(java.time.Duration.ofMinutes(60))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            return key;
        }
    }

    private String extractKeyFromUrl(String url) {
        if (url == null)
            return "";
        // If it starts with http and contains the bucket name (or just generic
        // heuristic), assume presigned
        // Simple heuristic: if it has '?' it's likely presigned.
        // We want the part after the last slash and before '?'
        if (url.startsWith("http") && url.contains("?")) {
            try {
                String temp = url.substring(0, url.indexOf("?"));
                return temp.substring(temp.lastIndexOf("/") + 1);
            } catch (Exception e) {
                return url;
            }
        }
        // If it's a full URL but no query params (public URL), extract filename
        if (url.startsWith("http")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return url;
    }
}