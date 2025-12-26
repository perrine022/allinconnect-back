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
            
            // 1. Clear tables with foreign keys to Users or Cards
            deviceTokenRepository.deleteAll();
            ratingRepository.deleteAll();
            savingRepository.deleteAll();
            paymentRepository.deleteAll();
            offerRepository.deleteAll();
            monthlyStatRepository.deleteAll();
            
            // 2. Break circular relationships
            userRepository.findAll().forEach(u -> {
                u.setFavorites(new ArrayList<>());
                u.setReferrer(null);
                u.setCard(null); // IMPORTANT: Unlink card before deleting it
                userRepository.save(u);
            });
            
            // 3. Clear the rest
            cardRepository.deleteAll();
            userRepository.deleteAll();
            subscriptionPlanRepository.deleteAll();

            log.info("Seeding database with complete profiles...");

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
            Double[] lats = {48.8566, 45.7640, 43.2965, 44.8378, 48.1173}; // Paris, Lyon, Marseille, Bordeaux, Rennes
            Double[] lons = {2.3522, 4.8357, 5.3698, -0.5792, -1.6778};

            List<User> pros = new ArrayList<>();
            for (int i = 0; i < professions.length; i++) {
                User pro = User.builder()
                        .firstName("Pro" + (i + 1))
                        .lastName("NomPro" + (i + 1))
                        .email("pro" + (i + 1) + "@example.com")
                        .password(passwordEncoder.encode("password"))
                        .address((i + 10) + " Rue")
                        .city(i == 2 ? "Marseille" : "Paris")
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
                    // Dates en juin 2026
                    offer.setStartDate(LocalDateTime.of(2026, 6, 1, 9, 0).plusDays(j * 2L));
                    offer.setEndDate(offer.getStartDate().plusMonths(1));
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
                    .address("10 Rue du Commerce")
                    .city("Paris")
                    .latitude(48.8566)
                    .longitude(2.3522)
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
            Card perrineCard = new Card("FAM-777-888", CardType.FAMILY, perrine);
            // On déclare les emails qui ont le droit de rejoindre cette carte famille
            perrineCard.setInvitedEmails(new ArrayList<>(List.of("filleul1@example.com", "filleul2@example.com", "filleul3@example.com")));
            cardRepository.save(perrineCard);
            perrine.setCard(perrineCard);
            userRepository.save(perrine);

            // --- Secondary Client User (Jean Dupont) ---
            User jean = User.builder()
                    .firstName("Jean")
                    .lastName("Dupont")
                    .email("jean.dupont@example.com")
                    .password(passwordEncoder.encode("password"))
                    .address("123 Rue de la Paix")
                    .city("Paris")
                    .latitude(48.8566)
                    .longitude(2.3522)
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
            Card jeanCard = new Card("IND-123-456", CardType.INDIVIDUAL, jean);
            cardRepository.save(jeanCard);
            jean.setCard(jeanCard);
            userRepository.save(jean);

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
            for (int i = 0; i < pros.size(); i++) {
                User pro = pros.get(i);
                Card proCard = new Card("PRO-CARD-" + (i + 1), CardType.INDIVIDUAL, pro);
                cardRepository.save(proCard);
                pro.setCard(proCard);
                // On met aussi une date de souscription pour les pros
                pro.setSubscriptionDate(LocalDateTime.now().minusMonths(6));
                pro.setSubscriptionAmount(pro.getSubscriptionPlan().getPrice());
                userRepository.save(pro);
            }

            // Add Favorites for Jean
            jean.setFavorites(new ArrayList<>(List.of(pros.get(0), pros.get(2))));
            userRepository.save(jean);

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
                referral.setCard(perrineCard);
                referral.setSubscriptionPlan(perrine.getSubscriptionPlan());
                referral.setSubscriptionType(SubscriptionType.PREMIUM);
                referral.setSubscriptionDate(LocalDateTime.now());
                
                userRepository.save(referral);
                
                // On les ajoute aussi à la liste des membres de la carte pour la cohérence bidirectionnelle
                perrineCard.getMembers().add(referral);
            }
            cardRepository.save(perrineCard);
            
            userRepository.save(jean);

            log.info("Seeding completed successfully with Jean Dupont as main client");
        };
    }
}
