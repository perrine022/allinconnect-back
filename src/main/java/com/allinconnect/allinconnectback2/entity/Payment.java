package com.allinconnect.allinconnectback2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"offers", "payments", "authorities", "password", "username"})
    private User user;

    public Payment() {}

    public Payment(Long id, Double amount, LocalDateTime paymentDate, User user) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
