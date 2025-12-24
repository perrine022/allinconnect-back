package com.allinconnect.allinconnectback2.entity;

import com.allinconnect.allinconnectback2.model.OfferStatus;
import com.allinconnect.allinconnectback2.model.OfferType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String imageUrl;
    private boolean isFeatured;

    @Enumerated(EnumType.STRING)
    private OfferType type = OfferType.OFFRE;

    @Enumerated(EnumType.STRING)
    private OfferStatus status = OfferStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"offers", "payments", "authorities", "password", "username"})
    private User professional;

    public Offer() {}

    public Offer(Long id, String title, String description, Double price, LocalDateTime startDate, LocalDateTime endDate, String imageUrl, boolean isFeatured, OfferType type, OfferStatus status, User professional) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
        this.type = type;
        this.status = status;
        this.professional = professional;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public OfferType getType() { return type; }
    public void setType(OfferType type) { this.type = type; }
    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }
    public OfferStatus getStatus() { return status; }
    public void setStatus(OfferStatus status) { this.status = status; }
    public User getProfessional() { return professional; }
    public void setProfessional(User professional) { this.professional = professional; }
}
