package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.Payment;
import com.allinconnect.allinconnectback2.entity.SubscriptionPlan;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.UserType;
import com.allinconnect.allinconnectback2.repository.PaymentRepository;
import com.allinconnect.allinconnectback2.repository.SubscriptionPlanRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionPlanRepository subscriptionPlanRepository, PaymentRepository paymentRepository, UserRepository userRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    private User ensureUser(User user) {
        if (user != null) return user;
        return userRepository.findAll().stream()
                .filter(u -> u.getUserType() == UserType.CLIENT)
                .findFirst()
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No user found in database")));
    }

    public List<SubscriptionPlan> getAllPlans() {
        log.debug("Service: Getting all subscription plans");
        return subscriptionPlanRepository.findAll();
    }

    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        log.debug("Service: Creating subscription plan {}", plan.getTitle());
        return subscriptionPlanRepository.save(plan);
    }

    public User subscribe(User user, Long planId) {
        user = ensureUser(user);
        log.debug("Service: Subscribing user {} to plan {}", user.getEmail(), planId);
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        
        user.setSubscriptionPlan(plan);
        user.setSubscriptionDate(LocalDateTime.now());
        
        double amount = plan.getPrice();
        
        // Remise de 50% pour le premier mois si l'utilisateur a un parrain et n'a pas encore de paiements
        if (user.getReferrer() != null && paymentRepository.findByUser(user).isEmpty()) {
            log.debug("Applying referral discount for user {}", user.getEmail());
            amount = amount * 0.5;
        }
        
        // Simuler un paiement lors de la souscription
        log.debug("Recording payment of {} for user {}", amount, user.getEmail());
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUser(user);
        paymentRepository.save(payment);
        
        return userRepository.save(user);
    }

    public List<Payment> getUserPayments(User user) {
        user = ensureUser(user);
        log.debug("Service: Getting payments for user {}", user.getEmail());
        return paymentRepository.findByUser(user);
    }
}
