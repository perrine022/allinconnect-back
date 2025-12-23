package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.Payment;
import com.allinconnect.allinconnectback2.entity.SubscriptionPlan;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.repository.PaymentRepository;
import com.allinconnect.allinconnectback2.repository.SubscriptionPlanRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionPlanRepository subscriptionPlanRepository, PaymentRepository paymentRepository, UserRepository userRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }

    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        return subscriptionPlanRepository.save(plan);
    }

    public User subscribe(User user, Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        
        user.setSubscriptionPlan(plan);
        user.setSubscriptionDate(LocalDateTime.now());
        
        double amount = plan.getPrice();
        
        // Remise de 50% pour le premier mois si l'utilisateur a un parrain et n'a pas encore de paiements
        if (user.getReferrer() != null && paymentRepository.findByUser(user).isEmpty()) {
            amount = amount * 0.5;
        }
        
        // Simuler un paiement lors de la souscription
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUser(user);
        paymentRepository.save(payment);
        
        return userRepository.save(user);
    }

    public List<Payment> getUserPayments(User user) {
        return paymentRepository.findByUser(user);
    }
}
