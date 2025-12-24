package com.allinconnect.allinconnectback2.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "savings")
public class Saving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    private String description;
    private Double amount;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"savings", "offers", "payments", "authorities", "password", "username", "card", "referrals", "favorites"})
    private User user;

    public Saving() {}

    public Saving(Long id, String shopName, String description, Double amount, LocalDateTime date, User user) {
        this.id = id;
        this.shopName = shopName;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
