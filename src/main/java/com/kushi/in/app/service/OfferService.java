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

    // ================== OFFERS ==================
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public Offer addOffer(Offer offer) {
        // ensure text not null
        if (offer.getText() == null) offer.setText("");
        return offerRepository.save(offer);
    }

    public Offer updateOffer(Long id, Offer updatedOffer) {
        Optional<Offer> optional = offerRepository.findById(id);
        if (optional.isPresent()) {
            Offer offer = optional.get();
            offer.setText(updatedOffer.getText());
            offer.setImageUrl(updatedOffer.getImageUrl());
            offer.setFontFamily(updatedOffer.getFontFamily());
            offer.setColor(updatedOffer.getColor());
            offer.setEmoji(updatedOffer.getEmoji());
            offer.setTitle(updatedOffer.getTitle());
            offer.setLink(updatedOffer.getLink());
            return offerRepository.save(offer);
        } else {
            throw new RuntimeException("Offer not found with id " + id);
        }
    }

    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    // ================== BANNERS ==================
    public List<Offer> getAllBanners() {
        return offerRepository.findAll().stream()
                .filter(o -> o.getImageUrl() != null && (o.getText() == null || o.getText().isEmpty()))
                .toList();
    }

    public Offer addBanner(Offer banner) {
        banner.setText(""); // banners text always empty
        return offerRepository.save(banner);
    }

    public Offer updateBanner(Long id, Offer updatedBanner) {
        Optional<Offer> optional = offerRepository.findById(id);
        if (optional.isPresent()) {
            Offer banner = optional.get();
            banner.setImageUrl(updatedBanner.getImageUrl());
            banner.setTitle(updatedBanner.getTitle());
            banner.setLink(updatedBanner.getLink());
            banner.setText(""); // keep text empty for banners
            return offerRepository.save(banner);
        } else {
            throw new RuntimeException("Banner not found with id " + id);
        }
    }

    public void deleteBanner(Long id) {
        offerRepository.deleteById(id);
    }
}
