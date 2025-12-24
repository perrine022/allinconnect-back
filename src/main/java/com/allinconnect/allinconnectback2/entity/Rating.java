package com.allinconnect.allinconnectback2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rater_id", "rated_id"})
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer score; // e.g., 1 to 5
    private String comment;

    @ManyToOne
    @JoinColumn(name = "rater_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"password", "email", "address", "city", "birthDate", "referrals", "favorites", "card", "payments", "authorities", "username", "subscriptionPlan", "savings"})
    private User rater;

    @ManyToOne
    @JoinColumn(name = "rated_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"password", "email", "address", "city", "birthDate", "referrals", "favorites", "card", "payments", "authorities", "username", "subscriptionPlan", "savings"})
    private User rated;

    private LocalDateTime createdAt;

    public Rating() {}

    public Rating(Long id, Integer score, String comment, User rater, User rated, LocalDateTime createdAt) {
        this.id = id;
        this.score = score;
        this.comment = comment;
        this.rater = rater;
        this.rated = rated;
        this.createdAt = createdAt;
    }

    public static RatingBuilder builder() {
        return new RatingBuilder();
    }

    public static class RatingBuilder {
        private Long id;
        private Integer score;
        private String comment;
        private User rater;
        private User rated;
        private LocalDateTime createdAt;

        public RatingBuilder id(Long id) { this.id = id; return this; }
        public RatingBuilder score(Integer score) { this.score = score; return this; }
        public RatingBuilder comment(String comment) { this.comment = comment; return this; }
        public RatingBuilder rater(User rater) { this.rater = rater; return this; }
        public RatingBuilder rated(User rated) { this.rated = rated; return this; }
        public RatingBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Rating build() { return new Rating(id, score, comment, rater, rated, createdAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public User getRater() { return rater; }
    public void setRater(User rater) { this.rater = rater; }
    public User getRated() { return rated; }
    public void setRated(User rated) { this.rated = rated; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
