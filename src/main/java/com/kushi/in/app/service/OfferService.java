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

    // ================== GET ALL OFFERS ==================
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    // ================== ADD OFFER ==================
    public Offer addOffer(Offer offer) {

        if (offer.getText() == null) offer.setText("");
        if (offer.getFontFamily() == null) offer.setFontFamily("Arial");
        if (offer.getColor() == null) offer.setColor("#000000");
        if (offer.getEmoji() == null) offer.setEmoji("");
        if (offer.getImageUrl() == null) offer.setImageUrl("");

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
            offer.setImageUrl(updatedOffer.getImageUrl());
        }

        return offerRepository.save(offer);
    }

    // ================== DELETE OFFER ==================
    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }
}
