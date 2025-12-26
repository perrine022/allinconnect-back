package com.allinconnect.allinconnectback2.config;

import com.allinconnect.allinconnectback2.entity.Card;
import com.allinconnect.allinconnectback2.entity.Offer;
import com.allinconnect.allinconnectback2.entity.Payment;
import com.allinconnect.allinconnectback2.entity.SubscriptionPlan;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.*;
import com.allinconnect.allinconnectback2.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final Random random = new Random();

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            OfferRepository offerRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            CardRepository cardRepository,
            PaymentRepository paymentRepository,
            SavingRepository savingRepository,
            RatingRepository ratingRepository,
            DeviceTokenRepository deviceTokenRepository,
            MonthlyStatRepository monthlyStatRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            log.info("Forcing database re-seed: Clearing all tables...");
            
            // 0. Break foreign key links using native SQL before clearing anything
            try {
                if (cardRepository.checkUsersTableExists() > 0) {
                    cardRepository.unlinkCardsFromUsers();
                }
                if (cardRepository.checkCardsTableExists() > 0) {
                    cardRepository.fixCardTypeColumn();
                }
                if (cardRepository.checkInvitedEmailsTableExists() > 0) {
                    cardRepository.truncateInvitedEmails();
                }
                if (cardRepository.checkCardsTableExists() > 0) {
                    cardRepository.truncateCards();
                }
            } catch (Exception e) {
                log.warn("Could not perform native SQL cleanup: {}", e.getMessage());
            }

            // 1. Clear tables with foreign keys to Users or Cards
            try {
                deviceTokenRepository.deleteAll();
                ratingRepository.deleteAll();
                savingRepository.deleteAll();
                paymentRepository.deleteAll();
                offerRepository.deleteAll();
                monthlyStatRepository.deleteAll();
            } catch (Exception e) {
                log.warn("Could not clear related tables: {}", e.getMessage());
            }
            
            // 2. Break other circular relationships (referral, favorites, members)
            try {
                if (cardRepository.checkUsersTableExists() > 0) {
                    userRepository.findAll().forEach(u -> {
                        u.setFavorites(new ArrayList<>());
                        u.setReferrer(null);
                        userRepository.save(u);
                    });
                }
            } catch (Exception e) {
                log.warn("Could not clear user relationships: {}", e.getMessage());
            }
            
            // 2.5 Clear the user-card members join table if it exists
            // This table is managed by @OneToMany mappedBy="card" in Card, so it's usually just card_id in users table.
            // But we already set card_id to NULL in step 0.
            
            // 3. Clear the rest
            try {
                userRepository.deleteAll();
                subscriptionPlanRepository.deleteAll();
            } catch (Exception e) {
                log.warn("Could not clear main tables: {}", e.getMessage());
            }

            log.info("Seeding database with complete profiles...");

            try {
                // --- Subscription Plans ---
                SubscriptionPlan proMonthly = new SubscriptionPlan(null, "Pro Mensuel", "Plan professionnel mensuel", 9.99, PlanCategory.PROFESSIONAL, PlanDuration.MONTHLY);
                SubscriptionPlan proAnnual = new SubscriptionPlan(null, "Pro Annuel", "Plan professionnel annuel", 99.0, PlanCategory.PROFESSIONAL, PlanDuration.ANNUAL);
                SubscriptionPlan clientIndividual = new SubscriptionPlan(null, "Client Individuel", "Plan client individuel", 2.99, PlanCategory.INDIVIDUAL, PlanDuration.MONTHLY);
                SubscriptionPlan clientIndividualAnnual = new SubscriptionPlan(null, "Client Individuel Annuel", "Plan client individuel annuel", 29.99, PlanCategory.INDIVIDUAL, PlanDuration.ANNUAL);
                SubscriptionPlan clientFamily = new SubscriptionPlan(null, "Client Famille", "Plan client famille (jusqu'à 4)", 9.99, PlanCategory.FAMILY, PlanDuration.MONTHLY);
                
                subscriptionPlanRepository.saveAll(List.of(proMonthly, proAnnual, clientIndividual, clientIndividualAnnual, clientFamily));

                // --- Users (Professionals) ---
                String[] professions = {"Coiffeur", "Boulanger", "Plombier", "Web Designer", "Consultant"};
                ProfessionCategory[] categories = {ProfessionCategory.BEAUTE_ESTHETIQUE, ProfessionCategory.FOOD_PLAISIRS, ProfessionCategory.SERVICE_PRATIQUES, ProfessionCategory.ENTRE_PROS, ProfessionCategory.SERVICE_PRATIQUES};
                String[] cities = {"Nice", "Cannes", "Saint-Tropez", "Menton", "Nice"};
                Double[] lats = {43.7102, 43.5528, 43.2727, 43.7745, 43.7102}; // Nice, Cannes, Saint-Tropez, Menton, Nice
                Double[] lons = {7.2620, 7.0174, 6.6405, 7.4975, 7.2620};

                List<User> pros = new ArrayList<>();
                for (int i = 0; i < professions.length; i++) {
                    User pro = User.builder()
                            .firstName("Pro" + (i + 1))
                            .lastName("NomPro" + (i + 1))
                            .email("pro" + (i + 1) + "@example.com")
                            .password(passwordEncoder.encode("password"))
                            .address((i + 10) + " Avenue Jean Médecin")
                            .city(cities[i])
                            .latitude(lats[i])
                            .longitude(lons[i])
                            .birthDate(LocalDate.now().minusYears(30 + i))
                            .userType(UserType.PROFESSIONAL)
                            .subscriptionType(SubscriptionType.PREMIUM)
                            .subscriptionPlan(i % 2 == 0 ? proMonthly : proAnnual)
                            .profession(professions[i])
                            .category(categories[i])
                            .establishmentName("Etablissement " + professions[i])
                            .establishmentDescription("Description de l'établissement de " + professions[i])
                            .phoneNumber("010203040" + i)
                            .website("https://www.pro" + (i + 1) + ".com")
                            .instagram("@pro" + (i + 1) + "_official")
                            .openingHours("Lun-Ven 9h-18h")
                            .hasConnectedBefore(true)
                            .build();
                    pro.setReferralCode("PROCODE" + (i + 1));
                    userRepository.save(pro);
                    pros.add(pro);

                    // --- Offers and Events for each pro ---
                    for (int j = 1; j <= 3; j++) {
                        Offer offer = new Offer();
                        boolean isEvent = (j == 3); // Le 3ème est un événement
                        offer.setTitle((isEvent ? "Evénement " : "Offre ") + professions[i] + " " + j);
                        offer.setDescription("Description détaillée pour " + (isEvent ? "l'événement " : "l'offre ") + j + " du professionnel " + professions[i]);
                        offer.setPrice(10.0 + random.nextInt(90));
                        // Dates commençant aujourd'hui pour être visibles immédiatement,
                        // mais s'étendant bien jusqu'en juin 2026 et au-delà.
                        offer.setStartDate(LocalDateTime.now().minusDays(1)); 
                        offer.setEndDate(LocalDateTime.of(2026, 12, 31, 23, 59));
                        offer.setType(isEvent ? OfferType.EVENEMENT : OfferType.OFFRE);
                        offer.setFeatured(random.nextBoolean());
                        offer.setStatus(OfferStatus.ACTIVE);
                        offer.setProfessional(pro);
                        offerRepository.save(offer);
                    }
                }

                // --- Main Client User (Perrine) ---
                User perrine = User.builder()
                        .firstName("Perrine")
                        .lastName("Honore")
                        .email("perrine@gmail.com")
                        .password(passwordEncoder.encode("Perrine"))
                        .address("10 Avenue Jean Médecin")
                        .city("Nice")
                        .latitude(43.7102)
                        .longitude(7.2620)
                        .birthDate(LocalDate.of(1995, 5, 20))
                        .userType(UserType.CLIENT)
                        .subscriptionType(SubscriptionType.PREMIUM)
                        .subscriptionPlan(clientFamily)
                        .subscriptionDate(LocalDateTime.now().minusMonths(1)) // Souscrit il y a 1 mois
                        .hasConnectedBefore(true)
                        .build();
                perrine.setRenewalDate(LocalDateTime.now().plusMonths(11)); // Renouvellement dans 11 mois
                perrine.setSubscriptionAmount(clientFamily.getPrice());
                perrine.setReferralCode("PERRINECODE");
                userRepository.save(perrine);

                // Add Card to Perrine (Family)
                Card perrineCard = new Card("FAM-777-888", CardType.CLIENT_FAMILY, perrine);
                // On déclare les emails qui ont le droit de rejoindre cette carte famille
                perrineCard.setInvitedEmails(new ArrayList<>(List.of("filleul1@example.com", "filleul2@example.com", "filleul3@example.com")));
                
                // Only save card if the invited emails table exists
                if (cardRepository.checkInvitedEmailsTableExists() > 0) {
                    cardRepository.save(perrineCard);
                    perrine.setCard(perrineCard);
                    userRepository.save(perrine);
                } else {
                    log.warn("Table card_invited_emails not found, skipping card seeding for Perrine");
                }

                // --- Secondary Client User (Jean Dupont) ---
                User jean = User.builder()
                        .firstName("Jean")
                        .lastName("Dupont")
                        .email("jean.dupont@example.com")
                        .password(passwordEncoder.encode("password"))
                        .address("5 Avenue de la Croisette")
                        .city("Cannes")
                        .latitude(43.5528)
                        .longitude(7.0174)
                        .birthDate(LocalDate.of(1990, 1, 1))
                        .userType(UserType.CLIENT)
                        .subscriptionType(SubscriptionType.PREMIUM)
                        .subscriptionPlan(clientIndividual)
                        .subscriptionDate(LocalDateTime.now().minusDays(15)) // Souscrit il y a 15 jours
                        .hasConnectedBefore(true)
                        .build();
                jean.setRenewalDate(LocalDateTime.now().plusDays(15)); // Renouvellement dans 15 jours
                jean.setSubscriptionAmount(clientIndividual.getPrice());
                jean.setReferralCode("JEANCODE");
                userRepository.save(jean);

                // Add Card to Jean (Individual)
                if (cardRepository.checkCardsTableExists() > 0) {
                    Card jeanCard = new Card("IND-123-456", CardType.CLIENT_INDIVIDUAL, jean);
                    cardRepository.save(jeanCard);
                    jean.setCard(jeanCard);
                    userRepository.save(jean);
                }

                // Add Payments for Jean
                Payment p1 = new Payment();
                p1.setAmount(2.99);
                p1.setPaymentDate(LocalDateTime.now().minusMonths(1));
                p1.setUser(jean);
                
                Payment p2 = new Payment();
                p2.setAmount(2.99);
                p2.setPaymentDate(LocalDateTime.now().minusDays(15));
                p2.setUser(jean);
                
                paymentRepository.saveAll(List.of(p1, p2));
                jean.setPayments(new ArrayList<>(List.of(p1, p2)));
                userRepository.save(jean);

                // --- Add Card to Professionals too ---
                if (cardRepository.checkCardsTableExists() > 0) {
                    for (int i = 0; i < pros.size(); i++) {
                        User pro = pros.get(i);
                        Card proCard = new Card("PRO-CARD-" + (i + 1), CardType.PROFESSIONAL, pro);
                        cardRepository.save(proCard);
                        pro.setCard(proCard);
                        // On met aussi une date de souscription pour les pros
                        pro.setSubscriptionDate(LocalDateTime.now().minusMonths(6));
                        pro.setSubscriptionAmount(pro.getSubscriptionPlan().getPrice());
                        userRepository.save(pro);
                    }
                }

                // Add Favorites for Jean
                if (cardRepository.checkUserFavoritesTableExists() > 0) {
                    jean.setFavorites(new ArrayList<>(List.of(pros.get(0), pros.get(2))));
                    userRepository.save(jean);
                } else {
                    log.warn("Table user_favorites not found, skipping favorites seeding");
                }

                // Add Referrals (Filleuls) et membres de la famille de Perrine
                for (int i = 1; i <= 3; i++) {
                    String email = "filleul" + i + "@example.com";
                    User referral = User.builder()
                            .firstName("Filleul" + i)
                            .lastName("Nom" + i)
                            .email(email)
                            .password(passwordEncoder.encode("password"))
                            .userType(UserType.CLIENT)
                            .hasConnectedBefore(false)
                            .build();
                    
                    // Jean est le parrain
                    referral.setReferrer(jean);
                    
                    // Ils sont TOUS rattachés à la carte famille de Perrine (car leurs emails sont dans perrineCard.invitedEmails)
                    if (perrineCard.getId() != null) {
                        referral.setCard(perrineCard);
                        referral.setSubscriptionPlan(perrine.getSubscriptionPlan());
                        referral.setSubscriptionType(SubscriptionType.PREMIUM);
                        referral.setSubscriptionDate(LocalDateTime.now());
                        
                        userRepository.save(referral);
                        
                        // On les ajoute aussi à la liste des membres de la carte pour la cohérence bidirectionnelle
                        perrineCard.getMembers().add(referral);
                    } else {
                        userRepository.save(referral);
                    }
                }
                if (perrineCard.getId() != null) {
                    cardRepository.save(perrineCard);
                }
                
                userRepository.save(jean);

                log.info("Seeding completed successfully with Jean Dupont as main client");
            } catch (Exception e) {
                log.error("Failed to seed database: {}", e.getMessage(), e);
            }
        };
    }
}
