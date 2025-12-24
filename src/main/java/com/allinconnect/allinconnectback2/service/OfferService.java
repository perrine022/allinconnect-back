package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.Offer;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.OfferStatus;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.UserType;
import com.allinconnect.allinconnectback2.repository.OfferRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferService {

    private static final Logger log = LoggerFactory.getLogger(OfferService.class);
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository, UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    private User ensureUser(User user) {
        if (user != null) return user;
        return userRepository.findAll().stream()
                .filter(u -> u.getUserType() == UserType.PROFESSIONAL)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No professional user found in database for default user"));
    }

    public Offer createOffer(Offer offer, User professional) {
        professional = ensureUser(professional);
        log.debug("Service: Creating offer for professional {}", professional.getEmail());
        if (professional.getUserType() != UserType.PROFESSIONAL) {
            log.debug("User {} is not a professional", professional.getEmail());
            throw new RuntimeException("Only professionals can create offers");
        }
        offer.setProfessional(professional);
        offer.setStatus(OfferStatus.ACTIVE);
        return offerRepository.save(offer);
    }

    public List<Offer> getAllOffers() {
        log.debug("Service: Getting all offers");
        return offerRepository.findAll();
    }

    public List<Offer> getOffersByFilters(String city, ProfessionCategory category, Long professionalId) {
        log.debug("Service: Getting offers with filters - city: {}, category: {}, professionalId: {}", city, category, professionalId);
        return offerRepository.findByFilters(city, category, professionalId, OfferStatus.ACTIVE);
    }

    public List<Offer> getOffersByProfessional(User professional) {
        professional = ensureUser(professional);
        log.debug("Service: Getting offers for professional {}", professional.getEmail());
        return offerRepository.findByProfessional(professional);
    }

    public List<Offer> getOffersByProfessionalId(Long professionalId) {
        log.debug("Service: Getting offers for professional ID {}", professionalId);
        return offerRepository.findByProfessionalId(professionalId);
    }

    public Offer getOfferById(Long id) {
        log.debug("Service: Getting offer by id {}", id);
        return offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
    }

    public Offer updateOffer(Long id, Offer offerDetails, User professional) {
        professional = ensureUser(professional);
        log.debug("Service: Updating offer {} for professional {}", id, professional.getEmail());
        Offer offer = getOfferById(id);
        if (!offer.getProfessional().getId().equals(professional.getId())) {
            log.debug("Professional {} does not own offer {}", professional.getEmail(), id);
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
        professional = ensureUser(professional);
        log.debug("Service: Archiving offer {} for professional {}", id, professional.getEmail());
        Offer offer = getOfferById(id);
        if (!offer.getProfessional().getId().equals(professional.getId())) {
            log.debug("Professional {} does not own offer {}", professional.getEmail(), id);
            throw new RuntimeException("You can only archive your own offers");
        }
        offer.setStatus(OfferStatus.PAST);
        offerRepository.save(offer);
    }

    public void deleteOffer(Long id, User professional) {
        professional = ensureUser(professional);
        log.debug("Service: Deleting offer {} for professional {}", id, professional.getEmail());
        Offer offer = getOfferById(id);
        if (!offer.getProfessional().getId().equals(professional.getId())) {
            log.debug("Professional {} does not own offer {}", professional.getEmail(), id);
            throw new RuntimeException("You can only delete your own offers");
        }
        offerRepository.delete(offer);
    }
}
