package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c JOIN c.invitedEmails e WHERE e = :email AND c.type = com.allinconnect.allinconnectback2.model.CardType.CLIENT_FAMILY")
    Optional<Card> findFamilyCardByInvitedEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET card_id = NULL", nativeQuery = true)
    void unlinkCardsFromUsers();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM card_invited_emails", nativeQuery = true)
    void truncateInvitedEmails();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cards", nativeQuery = true)
    void truncateCards();

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE cards MODIFY COLUMN type VARCHAR(50)", nativeQuery = true)
    void fixCardTypeColumn();

    @Transactional
    @Query(value = "SELECT count(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user_favorites'", nativeQuery = true)
    long checkUserFavoritesTableExists();

    @Transactional
    @Query(value = "SELECT count(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'card_invited_emails'", nativeQuery = true)
    long checkInvitedEmailsTableExists();

    @Transactional
    @Query(value = "SELECT count(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'cards'", nativeQuery = true)
    long checkCardsTableExists();

    @Transactional
    @Query(value = "SELECT count(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users'", nativeQuery = true)
    long checkUsersTableExists();
}
