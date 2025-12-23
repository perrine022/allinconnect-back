package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.Offer;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.OfferStatus;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.UserType;
import com.allinconnect.allinconnectback2.repository.OfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Offer createOffer(Offer offer, User professional) {
        if (professional.getUserType() != UserType.PROFESSIONAL) {
            throw new RuntimeException("Only professionals can create offers");
        }
        offer.setProfessional(professional);
        offer.setStatus(OfferStatus.ACTIVE);
        return offerRepository.save(offer);
    }

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public List<Offer> getOffersByFilters(String city, ProfessionCategory category, Long professionalId) {
        return offerRepository.findByFilters(city, category, professionalId, OfferStatus.ACTIVE);
    }

    public List<Offer> getOffersByProfessional(User professional) {
        return offerRepository.findByProfessional(professional);
    }

    public Offer getOfferById(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
    }

    public Offer updateOffer(Long id, Offer offerDetails, User professional) {
        Offer offer = getOfferById(id);
        if (!offer.getProfessional().getId().equals(professional.getId())) {
            throw new RuntimeException("You can only update your own offers");
        }
        offer.setTitle(offerDetails.getTitle());
        offer.setDescription(offerDetails.getDescription());
        offer.setPrice(offerDetails.getPrice());
        offer.setStartDate(offerDetails.getStartDate());
        offer.setEndDate(offerDetails.getEndDate());
        offer.setFeatured(offerDetails.isFeatured());
        offer.setStatus(OfferStatus.ACTIVE);
        
        return offerRepository.save(offer);
    }

    public void archiveOffer(Long id, User professional) {
        Offer offer = getOfferById(id);
        if (!offer.getProfessional().getId().equals(professional.getId())) {
            throw new RuntimeException("You can only archive your own offers");
        }
        offer.setStatus(OfferStatus.PAST);
        offerRepository.save(offer);
    }

    public void deleteOffer(Long id, User professional) {
        Offer offer = getOfferById(id);
        if (!offer.getProfessional().getId().equals(professional.getId())) {
            throw new RuntimeException("You can only delete your own offers");
        }
        offerRepository.delete(offer);
    }
}
