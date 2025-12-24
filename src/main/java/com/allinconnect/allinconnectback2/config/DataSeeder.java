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
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already seeded");
                return;
            }

            log.info("Seeding database with complete profiles...");

            // --- Subscription Plans ---
            SubscriptionPlan proMonthly = new SubscriptionPlan(null, "Pro Mensuel", "Plan professionnel mensuel", 29.99, PlanCategory.PROFESSIONAL, PlanDuration.MONTHLY);
            SubscriptionPlan proAnnual = new SubscriptionPlan(null, "Pro Annuel", "Plan professionnel annuel", 299.99, PlanCategory.PROFESSIONAL, PlanDuration.ANNUAL);
            SubscriptionPlan clientIndividual = new SubscriptionPlan(null, "Client Individuel", "Plan client individuel", 9.99, PlanCategory.INDIVIDUAL, PlanDuration.MONTHLY);
            SubscriptionPlan clientFamily = new SubscriptionPlan(null, "Client Famille", "Plan client famille (jusqu'à 4)", 19.99, PlanCategory.FAMILY, PlanDuration.MONTHLY);
            
            subscriptionPlanRepository.saveAll(List.of(proMonthly, proAnnual, clientIndividual, clientFamily));

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
                        .website("https://pro" + (i+1) + ".com")
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
                    offer.setStartDate(LocalDateTime.now());
                    offer.setEndDate(LocalDateTime.now().plusMonths(1));
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
                    .subscriptionDate(LocalDateTime.now().minusMonths(1))
                    .hasConnectedBefore(true)
                    .build();
            perrine.setRenewalDate(LocalDateTime.now().plusDays(20));
            perrine.setSubscriptionAmount(clientFamily.getPrice());
            perrine.setReferralCode("PERRINECODE");
            userRepository.save(perrine);

            // Add Card to Perrine (Family)
            Card perrineCard = new Card("FAM-777-888", CardType.FAMILY, perrine);
            cardRepository.save(perrineCard);
            perrine.setCard(perrineCard);
            userRepository.save(perrine);

            // --- Secondary Client User (Jean Dupont) ---
            User mainClient = User.builder()
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
                    .subscriptionDate(LocalDateTime.now().minusMonths(2))
                    .hasConnectedBefore(true)
                    .build();
            mainClient.setRenewalDate(LocalDateTime.now().plusDays(10));
            mainClient.setSubscriptionAmount(clientIndividual.getPrice());
            mainClient.setReferralCode("JEANCODE");
            userRepository.save(mainClient);

            // Add Payments for Jean
            Payment p1 = new Payment();
            p1.setAmount(9.99);
            p1.setPaymentDate(LocalDateTime.now().minusMonths(2));
            p1.setUser(mainClient);
            
            Payment p2 = new Payment();
            p2.setAmount(9.99);
            p2.setPaymentDate(LocalDateTime.now().minusMonths(1));
            p2.setUser(mainClient);
            
            paymentRepository.saveAll(List.of(p1, p2));
            mainClient.setPayments(new ArrayList<>(List.of(p1, p2)));

            // Add Card to Main Client
            Card mainCard = new Card("CARD-888-999", CardType.INDIVIDUAL, mainClient);
            cardRepository.save(mainCard);
            mainClient.setCard(mainCard);

            // Add Favorites (Pros)
            mainClient.setFavorites(new ArrayList<>(List.of(pros.get(0), pros.get(2))));

            // Add Referrals (Filleuls)
            for (int i = 1; i <= 3; i++) {
                User referral = User.builder()
                        .firstName("Filleul" + i)
                        .lastName("Nom" + i)
                        .email("filleul" + i + "@example.com")
                        .password(passwordEncoder.encode("password"))
                        .userType(UserType.CLIENT)
                        .hasConnectedBefore(false)
                        .build();
                referral.setReferrer(mainClient);
                userRepository.save(referral);
            }
            
            userRepository.save(mainClient);

            log.info("Seeding completed successfully with Jean Dupont as main client");
        };
    }
}
