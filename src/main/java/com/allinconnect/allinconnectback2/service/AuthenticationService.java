package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.dto.*;
import com.allinconnect.allinconnectback2.entity.Card;
import com.allinconnect.allinconnectback2.entity.SubscriptionPlan;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.event.ProfessionalCreatedEvent;
import com.allinconnect.allinconnectback2.model.CardType;
import com.allinconnect.allinconnectback2.model.PlanCategory;
import com.allinconnect.allinconnectback2.repository.CardRepository;
import com.allinconnect.allinconnectback2.repository.SubscriptionPlanRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import com.allinconnect.allinconnectback2.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CardRepository cardRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CardRepository cardRepository,
            SubscriptionPlanRepository subscriptionPlanRepository,
            org.springframework.context.ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.cardRepository = cardRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AuthResponse register(UserRegistrationRequest request) {
        log.debug("Service: Registering user {}", request.getEmail());
        String personalReferralCode = generateReferralCode(request.getLastName());
        
        var userBuilder = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .city(request.getCity())
                .birthDate(request.getBirthDate())
                .userType(request.getUserType())
                .subscriptionType(request.getSubscriptionType())
                .profession(request.getProfession())
                .category(request.getCategory())
                .hasConnectedBefore(false);

        if (request.getSubscriptionPlanId() != null) {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getSubscriptionPlanId())
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
            userBuilder.subscriptionPlan(plan);
            userBuilder.subscriptionDate(LocalDateTime.now());
        }
        
        User user = userBuilder.build();
        user.setReferralCode(personalReferralCode);

        // --- Vérification Invitation Carte Famille par Email ---
        cardRepository.findFamilyCardByInvitedEmail(request.getEmail()).ifPresent(familyCard -> {
            log.debug("User invited to family card: {}", familyCard.getCardNumber());
            user.setCard(familyCard);
            // On rattache l'abonnement du propriétaire
            if (familyCard.getOwner() != null && familyCard.getOwner().getSubscriptionPlan() != null) {
                user.setSubscriptionPlan(familyCard.getOwner().getSubscriptionPlan());
                user.setSubscriptionDate(LocalDateTime.now());
            }
        });

        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            log.debug("Applying referral code: {}", request.getReferralCode());
            userRepository.findByReferralCode(request.getReferralCode()).ifPresent(user::setReferrer);
        }

        // Logique de Carte
        if (request.getCardNumber() != null && !request.getCardNumber().isEmpty()) {
            // Tentative de rattachement à une carte existante (Famille)
            Card card = cardRepository.findByCardNumber(request.getCardNumber())
                    .orElseThrow(() -> new RuntimeException("Card not found with number: " + request.getCardNumber()));
            
            if (card.getType() == CardType.CLIENT_FAMILY) {
                if (card.getMembers().size() >= 4) {
                    throw new RuntimeException("Family card is full (max 4 members)");
                }
                user.setCard(card);
            } else {
                throw new RuntimeException("Cannot attach to an individual card");
            }
        }
        
        userRepository.save(user);

        if (user.getUserType() == com.allinconnect.allinconnectback2.model.UserType.PROFESSIONAL) {
            log.info("Publishing ProfessionalCreatedEvent for user ID: {}", user.getId());
            eventPublisher.publishEvent(new ProfessionalCreatedEvent(user.getId()));
        }

        // Si l'utilisateur a un plan et pas de carte (et n'est pas déjà rattaché à une carte famille), on lui crée sa propre carte
        if (user.getSubscriptionPlan() != null && user.getCard() == null) {
            CardType cardType;
            if (user.getUserType() == com.allinconnect.allinconnectback2.model.UserType.PROFESSIONAL) {
                cardType = CardType.PROFESSIONAL;
            } else {
                cardType = (user.getSubscriptionPlan().getCategory() == PlanCategory.FAMILY) ? CardType.CLIENT_FAMILY : CardType.CLIENT_INDIVIDUAL;
            }
            Card newCard = new Card(generateCardNumber(), cardType, user);
            cardRepository.save(newCard);
            user.setCard(newCard);
            userRepository.save(user);
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    private String generateCardNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateReferralCode(String lastName) {
        log.debug("Generating referral code for {}", lastName);
        StringBuilder code = new StringBuilder(lastName.toLowerCase());
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public AuthResponse authenticate(LoginRequest request) {
        log.debug("Service: Authenticating user {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", request.getEmail(), e.getMessage());
            throw e;
        }
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        
        if (!user.isHasConnectedBefore()) {
            log.debug("First connection for user {}, marking as connected", user.getEmail());
            user.setHasConnectedBefore(true);
            userRepository.save(user);
        }
        
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        log.debug("Service: Processing forgot password for {}", request.getEmail());
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // In a real app, send email here. For now, return the token for testing/demo.
        return token;
    }

    public void resetPassword(ResetPasswordRequest request) {
        log.debug("Service: Resetting password with token");
        var user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            log.debug("Token expired for user {}", user.getEmail());
            throw new RuntimeException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
