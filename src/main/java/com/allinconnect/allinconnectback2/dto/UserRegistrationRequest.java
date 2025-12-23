package com.allinconnect.allinconnectback2.dto;

import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.SubscriptionType;
import com.allinconnect.allinconnectback2.model.UserType;

import java.time.LocalDate;

public class UserRegistrationRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private LocalDate birthDate;
    private UserType userType;
    private SubscriptionType subscriptionType;
    private String profession;
    private ProfessionCategory category;
    private String referralCode;

    public UserRegistrationRequest() {}

    public UserRegistrationRequest(String email, String password, String firstName, String lastName, String address, String city, LocalDate birthDate, UserType userType, SubscriptionType subscriptionType, String profession, ProfessionCategory category, String referralCode) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.birthDate = birthDate;
        this.userType = userType;
        this.subscriptionType = subscriptionType;
        this.profession = profession;
        this.category = category;
        this.referralCode = referralCode;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public SubscriptionType getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(SubscriptionType subscriptionType) { this.subscriptionType = subscriptionType; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public ProfessionCategory getCategory() { return category; }
    public void setCategory(ProfessionCategory category) { this.category = category; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
}
