package com.allinconnect.allinconnectback2.entity;

import com.allinconnect.allinconnectback2.model.PlanCategory;
import com.allinconnect.allinconnectback2.model.PlanDuration;
import jakarta.persistence.*;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;

    @Enumerated(EnumType.STRING)
    private PlanCategory category;

    @Enumerated(EnumType.STRING)
    private PlanDuration duration;

    public SubscriptionPlan() {}

    public SubscriptionPlan(Long id, String title, String description, Double price, PlanCategory category, PlanDuration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.duration = duration;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public PlanCategory getCategory() { return category; }
    public void setCategory(PlanCategory category) { this.category = category; }
    public PlanDuration getDuration() { return duration; }
    public void setDuration(PlanDuration duration) { this.duration = duration; }
}
