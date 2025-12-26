package com.allinconnect.allinconnectback2.entity;

import com.allinconnect.allinconnectback2.model.CardType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    private CardType type;

    @OneToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"card", "offers", "payments", "authorities", "password", "username"})
    private User owner;

    @OneToMany(mappedBy = "card")
    @JsonIgnoreProperties({"card", "offers", "payments", "authorities", "password", "username"})
    private List<User> members = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "card_invited_emails", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "email")
    private List<String> invitedEmails = new ArrayList<>();

    public Card() {}

    public Card(String cardNumber, CardType type, User owner) {
        this.cardNumber = cardNumber;
        this.type = type;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public CardType getType() { return type; }
    public void setType(CardType type) { this.type = type; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
    public List<String> getInvitedEmails() { return invitedEmails; }
    public void setInvitedEmails(List<String> invitedEmails) { this.invitedEmails = invitedEmails; }
}
