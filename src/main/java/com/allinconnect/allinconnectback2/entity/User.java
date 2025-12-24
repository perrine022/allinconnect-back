package com.allinconnect.allinconnectback2.entity;

import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.SubscriptionType;
import com.allinconnect.allinconnectback2.model.UserType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private boolean hasConnectedBefore;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    private LocalDateTime subscriptionDate;
    private LocalDateTime renewalDate;
    private Double subscriptionAmount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Payment> payments;

    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Offer> offers;
    
    // Pour les professionnels
    private String profession;

    @Enumerated(EnumType.STRING)
    private ProfessionCategory category;

    private String establishmentName;
    private String establishmentDescription;
    private String phoneNumber;
    private String website;
    private String openingHours;

    public String getEstablishmentName() { return establishmentName; }
    public void setEstablishmentName(String establishmentName) { this.establishmentName = establishmentName; }
    public String getEstablishmentDescription() { return establishmentDescription; }
    public void setEstablishmentDescription(String establishmentDescription) { this.establishmentDescription = establishmentDescription; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    private String referralCode;

    @ManyToOne
    @JoinColumn(name = "referrer_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"referrals", "referrer"})
    private User referrer;

    @OneToMany(mappedBy = "referrer")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"referrer", "referrals"})
    private List<User> referrals;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("user")
    private List<Saving> savings = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "favorite_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<User> favorites;

    @ManyToOne
    @JoinColumn(name = "card_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("members")
    private Card card;

    public User() {}

    public User(Long id, String email, String password, String firstName, String lastName, String address, String city, Double latitude, Double longitude, LocalDate birthDate, UserType userType, boolean hasConnectedBefore, SubscriptionType subscriptionType, String profession, ProfessionCategory category) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.birthDate = birthDate;
        this.userType = userType;
        this.hasConnectedBefore = hasConnectedBefore;
        this.subscriptionType = subscriptionType;
        this.profession = profession;
        this.category = category;
    }

    public User(Long id, String email, String password, String firstName, String lastName, String address, String city, Double latitude, Double longitude, LocalDate birthDate, UserType userType, boolean hasConnectedBefore, SubscriptionType subscriptionType, SubscriptionPlan subscriptionPlan, LocalDateTime subscriptionDate, String profession, ProfessionCategory category, Card card) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.birthDate = birthDate;
        this.userType = userType;
        this.hasConnectedBefore = hasConnectedBefore;
        this.subscriptionType = subscriptionType;
        this.subscriptionPlan = subscriptionPlan;
        this.subscriptionDate = subscriptionDate;
        this.profession = profession;
        this.category = category;
        this.card = card;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String address;
        private String city;
        private Double latitude;
        private Double longitude;
        private LocalDate birthDate;
        private UserType userType;
        private boolean hasConnectedBefore;
        private SubscriptionType subscriptionType;
        private SubscriptionPlan subscriptionPlan;
        private LocalDateTime subscriptionDate;
        private String profession;
        private ProfessionCategory category;
        private String establishmentName;
        private String establishmentDescription;
        private String phoneNumber;
        private String website;
        private String openingHours;
        private Card card;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public UserBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public UserBuilder address(String address) { this.address = address; return this; }
        public UserBuilder city(String city) { this.city = city; return this; }
        public UserBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public UserBuilder longitude(Double longitude) { this.longitude = longitude; return this; }
        public UserBuilder birthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }
        public UserBuilder userType(UserType userType) { this.userType = userType; return this; }
        public UserBuilder hasConnectedBefore(boolean hasConnectedBefore) { this.hasConnectedBefore = hasConnectedBefore; return this; }
        public UserBuilder subscriptionType(SubscriptionType subscriptionType) { this.subscriptionType = subscriptionType; return this; }
        public UserBuilder subscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; return this; }
        public UserBuilder subscriptionDate(LocalDateTime subscriptionDate) { this.subscriptionDate = subscriptionDate; return this; }
        public UserBuilder profession(String profession) { this.profession = profession; return this; }
        public UserBuilder category(ProfessionCategory category) { this.category = category; return this; }
        public UserBuilder establishmentName(String establishmentName) { this.establishmentName = establishmentName; return this; }
        public UserBuilder establishmentDescription(String establishmentDescription) { this.establishmentDescription = establishmentDescription; return this; }
        public UserBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public UserBuilder website(String website) { this.website = website; return this; }
        public UserBuilder openingHours(String openingHours) { this.openingHours = openingHours; return this; }
        public UserBuilder card(Card card) { this.card = card; return this; }

        public User build() {
            return new User(id, email, password, firstName, lastName, address, city, latitude, longitude, birthDate, userType, hasConnectedBefore, subscriptionType, subscriptionPlan, subscriptionDate, profession, category, card);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public boolean isHasConnectedBefore() { return hasConnectedBefore; }
    public void setHasConnectedBefore(boolean hasConnectedBefore) { this.hasConnectedBefore = hasConnectedBefore; }
    public SubscriptionType getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(SubscriptionType subscriptionType) { this.subscriptionType = subscriptionType; }
    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public LocalDateTime getSubscriptionDate() { return subscriptionDate; }
    public void setSubscriptionDate(LocalDateTime subscriptionDate) { this.subscriptionDate = subscriptionDate; }
    public LocalDateTime getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDateTime renewalDate) { this.renewalDate = renewalDate; }
    public Double getSubscriptionAmount() { return subscriptionAmount; }
    public void setSubscriptionAmount(Double subscriptionAmount) { this.subscriptionAmount = subscriptionAmount; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public List<Offer> getOffers() { return offers; }
    public void setOffers(List<Offer> offers) { this.offers = offers; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public List<User> getFavorites() { return favorites; }
    public void setFavorites(List<User> favorites) { this.favorites = favorites; }

    public ProfessionCategory getCategory() { return category; }
    public void setCategory(ProfessionCategory category) { this.category = category; }

    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public User getReferrer() { return referrer; }
    public void setReferrer(User referrer) { this.referrer = referrer; }
    public List<User> getReferrals() { return referrals; }
    public void setReferrals(List<User> referrals) { this.referrals = referrals; }
    public List<Saving> getSavings() { return savings; }
    public void setSavings(List<Saving> savings) { this.savings = savings; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userType.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
