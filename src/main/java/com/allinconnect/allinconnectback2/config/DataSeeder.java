package com.allinconnect.allinconnectback2.config;

import com.allinconnect.allinconnectback2.entity.Offer;
import com.allinconnect.allinconnectback2.entity.SubscriptionPlan;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.*;
import com.allinconnect.allinconnectback2.repository.OfferRepository;
import com.allinconnect.allinconnectback2.repository.SubscriptionPlanRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already seeded");
                return;
            }

            log.info("Seeding database...");

            // --- Subscription Plans ---
            SubscriptionPlan proMonthly = new SubscriptionPlan(null, "Pro Mensuel", "Plan professionnel mensuel", 29.99, PlanCategory.PROFESSIONAL, PlanDuration.MONTHLY);
            SubscriptionPlan proAnnual = new SubscriptionPlan(null, "Pro Annuel", "Plan professionnel annuel", 299.99, PlanCategory.PROFESSIONAL, PlanDuration.ANNUAL);
            SubscriptionPlan clientIndividual = new SubscriptionPlan(null, "Client Individuel", "Plan client individuel", 9.99, PlanCategory.INDIVIDUAL, PlanDuration.MONTHLY);
            SubscriptionPlan clientFamily = new SubscriptionPlan(null, "Client Famille", "Plan client famille (jusqu'à 4)", 19.99, PlanCategory.FAMILY, PlanDuration.MONTHLY);
            
            subscriptionPlanRepository.saveAll(List.of(proMonthly, proAnnual, clientIndividual, clientFamily));

            // --- Users (Clients) ---
            for (int i = 1; i <= 5; i++) {
                User client = User.builder()
                        .firstName("Client" + i)
                        .lastName("NomClient" + i)
                        .email("client" + i + "@example.com")
                        .password(passwordEncoder.encode("password"))
                        .address(i + " Rue de la Paix")
                        .city("Paris")
                        .birthDate(LocalDate.now().minusYears(20 + i))
                        .userType(UserType.CLIENT)
                        .subscriptionType(SubscriptionType.FREE)
                        .hasConnectedBefore(true)
                        .build();
                client.setReferralCode("CLIENTCODE" + i);
                userRepository.save(client);
            }

            // --- Users (Professionals) ---
            String[] professions = {"Coiffeur", "Boulanger", "Plombier", "Web Designer", "Consultant"};
            ProfessionCategory[] categories = {ProfessionCategory.BEAUTE_ESTHETIQUE, ProfessionCategory.FOOD_PLAISIRS, ProfessionCategory.SERVICE_PRATIQUES, ProfessionCategory.ENTRE_PROS, ProfessionCategory.SERVICE_PRATIQUES};

            for (int i = 0; i < professions.length; i++) {
                User pro = User.builder()
                        .firstName("Pro" + (i + 1))
                        .lastName("NomPro" + (i + 1))
                        .email("pro" + (i + 1) + "@example.com")
                        .password(passwordEncoder.encode("password"))
                        .address((i + 10) + " Avenue des Champs")
                        .city("Lyon")
                        .birthDate(LocalDate.now().minusYears(30 + i))
                        .userType(UserType.PROFESSIONAL)
                        .subscriptionType(SubscriptionType.PREMIUM)
                        .subscriptionPlan(i % 2 == 0 ? proMonthly : proAnnual)
                        .profession(professions[i])
                        .category(categories[i])
                        .hasConnectedBefore(true)
                        .build();
                pro.setReferralCode("PROCODE" + (i + 1));
                userRepository.save(pro);

                // --- Offers for each pro ---
                for (int j = 1; j <= 3; j++) {
                    Offer offer = new Offer();
                    offer.setTitle("Offre " + professions[i] + " " + j);
                    offer.setDescription("Description détaillée pour l'offre " + j + " du professionnel " + professions[i]);
                    offer.setPrice(10.0 + random.nextInt(90));
                    offer.setStartDate(LocalDate.now());
                    offer.setEndDate(LocalDate.now().plusMonths(1));
                    offer.setFeatured(random.nextBoolean());
                    offer.setStatus(OfferStatus.ACTIVE);
                    offer.setProfessional(pro);
                    offerRepository.save(offer);
                }
            }

            log.info("Seeding completed successfully");
        };
    }
}
