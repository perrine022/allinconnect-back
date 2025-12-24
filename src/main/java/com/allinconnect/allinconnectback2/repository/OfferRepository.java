package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.Offer;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.OfferStatus;
import com.allinconnect.allinconnectback2.model.OfferType;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByProfessional(User professional);
    List<Offer> findByProfessionalId(Long professionalId);

    @Query("SELECT o FROM Offer o WHERE " +
           "(:city IS NULL OR o.professional.city = :city) AND " +
           "(:category IS NULL OR o.professional.category = :category) AND " +
           "(:professionalId IS NULL OR o.professional.id = :professionalId) AND " +
           "(:type IS NULL OR o.type = :type) AND " +
           "(o.status = :status)")
    List<Offer> findByFilters(
            @Param("city") String city,
            @Param("category") ProfessionCategory category,
            @Param("professionalId") Long professionalId,
            @Param("type") OfferType type,
            @Param("status") OfferStatus status
    );
}
