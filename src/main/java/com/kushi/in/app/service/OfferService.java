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
<<<<<<< HEAD
        // ensure text not null
        if (offer.getText() == null) offer.setText("");
        return offerRepository.save(offer);
    }
=======
        return offerRepository.save(offer);
    }

>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
    public Offer updateOffer(Long id, Offer updatedOffer) {
        Optional<Offer> optional = offerRepository.findById(id);
        if (optional.isPresent()) {
            Offer offer = optional.get();
            offer.setText(updatedOffer.getText());
            offer.setImageUrl(updatedOffer.getImageUrl());
            offer.setFontFamily(updatedOffer.getFontFamily());
            offer.setColor(updatedOffer.getColor());
            offer.setEmoji(updatedOffer.getEmoji());
<<<<<<< HEAD
            offer.setTitle(updatedOffer.getTitle());
            offer.setLink(updatedOffer.getLink());
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
            return offerRepository.save(offer);
        } else {
            throw new RuntimeException("Offer not found with id " + id);
        }
    }

    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    // ================== BANNERS ==================
    // Get banners (offers with only imageUrl or a separate isBanner field)
    public List<Offer> getAllBanners() {
        // Example: banners are offers with non-null imageUrl
        return offerRepository.findAll().stream()
                .filter(o -> o.getImageUrl() != null && (o.getText() == null || o.getText().isEmpty()))
                .toList();
    }

    public Offer addBanner(Offer banner) {
        // Ensure text is empty for banners
        banner.setText("");
        return offerRepository.save(banner);
    }

    public Offer updateBanner(Long id, Offer updatedBanner) {
        Optional<Offer> optional = offerRepository.findById(id);
        if (optional.isPresent()) {
            Offer banner = optional.get();
            banner.setImageUrl(updatedBanner.getImageUrl());
<<<<<<< HEAD
            banner.setTitle(updatedBanner.getTitle());
            banner.setLink(updatedBanner.getLink());
            // keep text empty
            banner.setText("");
=======
>>>>>>> f0144ebd8f89dd88c5fff2bf7939a03f55b7b788
            return offerRepository.save(banner);
        } else {
            throw new RuntimeException("Banner not found with id " + id);
        }
    }

    public void deleteBanner(Long id) {
        offerRepository.deleteById(id);
    }
}
