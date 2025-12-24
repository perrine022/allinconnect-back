package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.entity.Saving;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.service.SavingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savings")
public class SavingController {

    private final SavingService savingService;

    public SavingController(SavingService savingService) {
        this.savingService = savingService;
    }

    @PostMapping
    public ResponseEntity<Saving> addSaving(@RequestBody Saving saving, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(savingService.addSaving(saving, user));
    }

    @GetMapping
    public ResponseEntity<List<Saving>> getMySavings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(savingService.getAllSavingsByUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Saving> updateSaving(@PathVariable Long id, @RequestBody Saving saving, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(savingService.updateSaving(id, saving, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaving(@PathVariable Long id, @AuthenticationPrincipal User user) {
        savingService.deleteSaving(id, user);
        return ResponseEntity.noContent().build();
    }
}
