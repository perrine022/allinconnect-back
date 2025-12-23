package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.entity.Offer;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.service.OfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer, @AuthenticationPrincipal User professional) {
        return ResponseEntity.ok(offerService.createOffer(offer, professional));
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ProfessionCategory category,
            @RequestParam(required = false) Long professionalId) {
        if (city != null || category != null || professionalId != null) {
            return ResponseEntity.ok(offerService.getOffersByFilters(city, category, professionalId));
        }
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @GetMapping("/my-offers")
    public ResponseEntity<List<Offer>> getMyOffers(@AuthenticationPrincipal User professional) {
        return ResponseEntity.ok(offerService.getOffersByProfessional(professional));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offer, @AuthenticationPrincipal User professional) {
        return ResponseEntity.ok(offerService.updateOffer(id, offer, professional));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveOffer(@PathVariable Long id, @AuthenticationPrincipal User professional) {
        offerService.archiveOffer(id, professional);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id, @AuthenticationPrincipal User professional) {
        offerService.deleteOffer(id, professional);
        return ResponseEntity.noContent().build();
    }
}
