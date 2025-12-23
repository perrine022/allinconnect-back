package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.dto.ChangePasswordRequest;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request
    ) {
        log.debug("Changing password for user: {}", user.getEmail());
        userService.changePassword(user, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/professionals")
    public ResponseEntity<List<User>> getProfessionalsByCity(@RequestParam String city) {
        log.debug("Getting professionals for city: {}", city);
        return ResponseEntity.ok(userService.findProfessionalsByCity(city));
    }

    @GetMapping("/professionals/search")
    public ResponseEntity<List<User>> searchProfessionals(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ProfessionCategory category) {
        log.debug("Searching professionals with city: {} and category: {}", city, category);
        return ResponseEntity.ok(userService.searchProfessionals(city, category));
    }

    @GetMapping("/professionals/categories")
    public ResponseEntity<List<ProfessionCategory>> getCategories() {
        log.debug("Getting all professional categories");
        return ResponseEntity.ok(Arrays.asList(ProfessionCategory.values()));
    }

    @PostMapping("/favorites/{favoriteId}")
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long favoriteId) {
        log.debug("Adding favorite {} to user: {}", favoriteId, user.getEmail());
        userService.addFavorite(user, favoriteId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites/{favoriteId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long favoriteId) {
        log.debug("Removing favorite {} from user: {}", favoriteId, user.getEmail());
        userService.removeFavorite(user, favoriteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<User>> getFavorites(@AuthenticationPrincipal User user) {
        log.debug("Getting favorites for user: {}", user.getEmail());
        return ResponseEntity.ok(userService.getFavorites(user));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal User user) {
        log.debug("Getting profile for user: {}", user.getEmail());
        return ResponseEntity.ok(user);
    }
}
