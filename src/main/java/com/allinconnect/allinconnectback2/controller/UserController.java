package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.dto.ChangePasswordRequest;
import com.allinconnect.allinconnectback2.dto.UserLightResponse;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
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
    public ResponseEntity<List<User>> getAllProfessionals() {
        log.debug("Getting all professionals");
        return ResponseEntity.ok(userService.findAllProfessionals());
    }

    @GetMapping("/professionals/by-city")
    public ResponseEntity<List<User>> getProfessionalsByCity(@RequestParam String city) {
        log.debug("Getting professionals for city: {}", city);
        return ResponseEntity.ok(userService.findProfessionalsByCity(city));
    }

    @GetMapping("/professionals/search")
    public ResponseEntity<List<User>> searchProfessionals(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ProfessionCategory category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double radius) {
        log.info("SEARCH REQUEST [Professional]: city={}, category={}, name={}, lat={}, lon={}, radius={}", 
                city, category, name, lat, lon, radius);
        return ResponseEntity.ok(userService.searchProfessionals(city, category, name, lat, lon, radius));
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

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody User profileDetails) {
        log.debug("Updating profile for user: {}", user != null ? user.getEmail() : "anonymous");
        return ResponseEntity.ok(userService.updateProfile(user, profileDetails));
    }

    @GetMapping("/me/light")
    public ResponseEntity<UserLightResponse> getMyProfileLight(@AuthenticationPrincipal User user) {
        log.debug("Getting light profile for user: {}", user != null ? user.getEmail() : "anonymous");
        return ResponseEntity.ok(userService.getUserLightInfo(user));
    }
}
