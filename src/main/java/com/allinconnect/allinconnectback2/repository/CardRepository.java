package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c JOIN c.invitedEmails e WHERE e = :email AND c.type = 'FAMILY'")
    Optional<Card> findFamilyCardByInvitedEmail(@Param("email") String email);
}
