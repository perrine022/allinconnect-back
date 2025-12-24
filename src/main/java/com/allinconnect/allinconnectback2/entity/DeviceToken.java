package com.allinconnect.allinconnectback2.entity;

import com.allinconnect.allinconnectback2.model.DeviceEnvironment;
import com.allinconnect.allinconnectback2.model.DevicePlatform;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_tokens")
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    private DevicePlatform platform;

    @Enumerated(EnumType.STRING)
    private DeviceEnvironment environment;

    private boolean active = true;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"deviceTokens", "password", "authorities"})
    private User user;

    public DeviceToken() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public DevicePlatform getPlatform() { return platform; }
    public void setPlatform(DevicePlatform platform) { this.platform = platform; }
    public DeviceEnvironment getEnvironment() { return environment; }
    public void setEnvironment(DeviceEnvironment environment) { this.environment = environment; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
