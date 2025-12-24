package com.allinconnect.allinconnectback2.dto;

import com.allinconnect.allinconnectback2.entity.Card;
import com.allinconnect.allinconnectback2.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class UserLightResponse {
    private String firstName;
    private String lastName;
    private boolean isMember;
    private Card card;
    private boolean isCardActive;
    private int referralCount;
    private int favoriteCount;
    private LocalDateTime subscriptionDate;
    private LocalDateTime renewalDate;
    private Double subscriptionAmount;
    private List<Payment> payments;

    public UserLightResponse() {}

    public UserLightResponse(String firstName, String lastName, boolean isMember, Card card, boolean isCardActive, int referralCount, int favoriteCount, LocalDateTime subscriptionDate, LocalDateTime renewalDate, Double subscriptionAmount, List<Payment> payments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isMember = isMember;
        this.card = card;
        this.isCardActive = isCardActive;
        this.referralCount = referralCount;
        this.favoriteCount = favoriteCount;
        this.subscriptionDate = subscriptionDate;
        this.renewalDate = renewalDate;
        this.subscriptionAmount = subscriptionAmount;
        this.payments = payments;
    }

    public static UserLightResponseBuilder builder() {
        return new UserLightResponseBuilder();
    }

    public static class UserLightResponseBuilder {
        private String firstName;
        private String lastName;
        private boolean isMember;
        private Card card;
        private boolean isCardActive;
        private int referralCount;
        private int favoriteCount;
        private LocalDateTime subscriptionDate;
        private LocalDateTime renewalDate;
        private Double subscriptionAmount;
        private List<Payment> payments;

        public UserLightResponseBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public UserLightResponseBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public UserLightResponseBuilder isMember(boolean isMember) { this.isMember = isMember; return this; }
        public UserLightResponseBuilder card(Card card) { this.card = card; return this; }
        public UserLightResponseBuilder isCardActive(boolean isCardActive) { this.isCardActive = isCardActive; return this; }
        public UserLightResponseBuilder referralCount(int referralCount) { this.referralCount = referralCount; return this; }
        public UserLightResponseBuilder favoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; return this; }
        public UserLightResponseBuilder subscriptionDate(LocalDateTime subscriptionDate) { this.subscriptionDate = subscriptionDate; return this; }
        public UserLightResponseBuilder renewalDate(LocalDateTime renewalDate) { this.renewalDate = renewalDate; return this; }
        public UserLightResponseBuilder subscriptionAmount(Double subscriptionAmount) { this.subscriptionAmount = subscriptionAmount; return this; }
        public UserLightResponseBuilder payments(List<Payment> payments) { this.payments = payments; return this; }

        public UserLightResponse build() {
            return new UserLightResponse(firstName, lastName, isMember, card, isCardActive, referralCount, favoriteCount, subscriptionDate, renewalDate, subscriptionAmount, payments);
        }
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public boolean isMember() { return isMember; }
    public void setMember(boolean member) { isMember = member; }
    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }
    public boolean isCardActive() { return isCardActive; }
    public void setCardActive(boolean cardActive) { isCardActive = cardActive; }
    public int getReferralCount() { return referralCount; }
    public void setReferralCount(int referralCount) { this.referralCount = referralCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    public LocalDateTime getSubscriptionDate() { return subscriptionDate; }
    public void setSubscriptionDate(LocalDateTime subscriptionDate) { this.subscriptionDate = subscriptionDate; }
    public LocalDateTime getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDateTime renewalDate) { this.renewalDate = renewalDate; }
    public Double getSubscriptionAmount() { return subscriptionAmount; }
    public void setSubscriptionAmount(Double subscriptionAmount) { this.subscriptionAmount = subscriptionAmount; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}
