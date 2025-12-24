package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.entity.Offer;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.OfferType;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {

    private static final Logger log = LoggerFactory.getLogger(OfferController.class);
    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer, @AuthenticationPrincipal User professional) {
        log.debug("Creating offer/event for professional: {}", professional.getEmail());
        return ResponseEntity.ok(offerService.createOffer(offer, professional));
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ProfessionCategory category,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) OfferType type) {
        log.debug("Getting all offers with filters - city: {}, category: {}, professionalId: {}, type: {}", city, category, professionalId, type);
        if (city != null || category != null || professionalId != null || type != null) {
            return ResponseEntity.ok(offerService.getOffersByFilters(city, category, professionalId, type));
        }
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @GetMapping("/my-offers")
    public ResponseEntity<List<Offer>> getMyOffers(@AuthenticationPrincipal User professional) {
        log.debug("Getting offers for professional: {}", professional.getEmail());
        return ResponseEntity.ok(offerService.getOffersByProfessional(professional));
    }

    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<Offer>> getOffersByProfessional(@PathVariable Long professionalId) {
        log.debug("Getting offers for professional ID: {}", professionalId);
        return ResponseEntity.ok(offerService.getOffersByProfessionalId(professionalId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        log.debug("Getting offer by id: {}", id);
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offer, @AuthenticationPrincipal User professional) {
        log.debug("Updating offer {} for professional: {}", id, professional.getEmail());
        return ResponseEntity.ok(offerService.updateOffer(id, offer, professional));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveOffer(@PathVariable Long id, @AuthenticationPrincipal User professional) {
        log.debug("Archiving offer {} for professional: {}", id, professional.getEmail());
        offerService.archiveOffer(id, professional);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id, @AuthenticationPrincipal User professional) {
        log.debug("Deleting offer {} for professional: {}", id, professional.getEmail());
        offerService.deleteOffer(id, professional);
        return ResponseEntity.noContent().build();
    }
}
