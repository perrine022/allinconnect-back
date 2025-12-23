package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.entity.Payment;
import com.allinconnect.allinconnectback2.entity.SubscriptionPlan;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlan>> getAllPlans() {
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }

    @PostMapping("/plans")
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody SubscriptionPlan plan) {
        return ResponseEntity.ok(subscriptionService.createPlan(plan));
    }

    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<User> subscribe(@AuthenticationPrincipal User user, @PathVariable Long planId) {
        return ResponseEntity.ok(subscriptionService.subscribe(user, planId));
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<Payment>> getMyPayments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(subscriptionService.getUserPayments(user));
    }
}
