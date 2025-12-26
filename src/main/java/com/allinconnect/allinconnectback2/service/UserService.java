package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.dto.ChangePasswordRequest;
import com.allinconnect.allinconnectback2.dto.UserLightResponse;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.UserType;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User ensureUser(User user) {
        if (user != null) return user;
        log.warn("AuthenticationPrincipal is null! Using fallback user (only for debug)");
        return userRepository.findAll().stream()
                .filter(u -> u.getUserType() == UserType.CLIENT)
                .findFirst()
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No user found in database")));
    }

    public void changePassword(User user, ChangePasswordRequest request) {
        user = ensureUser(user);
        log.debug("Service: Changing password for user {}", user.getEmail());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.debug("Invalid old password for user {}", user.getEmail());
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findProfessionalsByCity(String city) {
        log.debug("Service: Finding professionals in city {}", city);
        return userRepository.findByUserTypeAndCity(UserType.PROFESSIONAL, city);
    }

    @Transactional(readOnly = true)
    public List<User> searchProfessionals(String city, ProfessionCategory category, String name, Double lat, Double lon, Double radius) {
        log.debug("Service: Searching professionals with city: {}, category: {}, name: {}, lat: {}, lon: {}, radius: {}", city, category, name, lat, lon, radius);
        
        List<User> professionals = findAllProfessionals();

        return professionals.stream()
                .filter(pro -> {
                    // Filter by city (exact match)
                    if (city != null && !city.equalsIgnoreCase(pro.getCity())) return false;
                    
                    // Filter by category
                    if (category != null && pro.getCategory() != category) return false;
                    
                    // Filter by name (firstName or lastName)
                    if (name != null) {
                        String lowerName = name.toLowerCase();
                        boolean match = (pro.getFirstName() != null && pro.getFirstName().toLowerCase().contains(lowerName)) ||
                                        (pro.getLastName() != null && pro.getLastName().toLowerCase().contains(lowerName));
                        if (!match) return false;
                    }
                    
                    // Filter by radius (if coordinates and radius are provided)
                    if (lat != null && lon != null && radius != null && pro.getLatitude() != null && pro.getLongitude() != null) {
                        double distance = calculateDistance(lat, lon, pro.getLatitude(), pro.getLongitude());
                        if (distance > radius) return false;
                    }
                    
                    return true;
                })
                .toList();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Transactional(readOnly = true)
    public List<User> findAllProfessionals() {
        log.debug("Service: Finding all professionals");
        return userRepository.findAll().stream()
                .filter(user -> user.getUserType() == UserType.PROFESSIONAL)
                .toList();
    }

    public void addFavorite(User user, Long favoriteId) {
        user = ensureUser(user);
        log.debug("Service: Adding favorite {} to user {}", favoriteId, user.getEmail());
        User favorite = userRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite user not found"));
        
        if (user.getFavorites().contains(favorite)) {
            log.debug("User {} already has {} in favorites", user.getEmail(), favoriteId);
            throw new RuntimeException("User already in favorites");
        }
        
        user.getFavorites().add(favorite);
        userRepository.save(user);
    }

    public void removeFavorite(User user, Long favoriteId) {
        user = ensureUser(user);
        log.debug("Service: Removing favorite {} from user {}", favoriteId, user.getEmail());
        User favorite = userRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite user not found"));
        
        user.getFavorites().remove(favorite);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getFavorites(User user) {
        user = ensureUser(user);
        log.debug("Service: Getting favorites for user {}", user.getEmail());
        return user.getFavorites();
    }

    public User updateProfile(User user, User details) {
        user = ensureUser(user);
        log.debug("Service: Updating profile for user {}", user.getEmail());
        
        if (details.getFirstName() != null) user.setFirstName(details.getFirstName());
        if (details.getLastName() != null) user.setLastName(details.getLastName());
        if (details.getAddress() != null) user.setAddress(details.getAddress());
        if (details.getCity() != null) user.setCity(details.getCity());
        if (details.getLatitude() != null) user.setLatitude(details.getLatitude());
        if (details.getLongitude() != null) user.setLongitude(details.getLongitude());
        if (details.getBirthDate() != null) user.setBirthDate(details.getBirthDate());
        if (details.getProfession() != null) user.setProfession(details.getProfession());
        if (details.getCategory() != null) user.setCategory(details.getCategory());
        if (details.getEstablishmentName() != null) user.setEstablishmentName(details.getEstablishmentName());
        if (details.getEstablishmentDescription() != null) user.setEstablishmentDescription(details.getEstablishmentDescription());
        if (details.getPhoneNumber() != null) user.setPhoneNumber(details.getPhoneNumber());
        if (details.getWebsite() != null) user.setWebsite(details.getWebsite());
        if (details.getInstagram() != null) user.setInstagram(details.getInstagram());
        if (details.getOpeningHours() != null) user.setOpeningHours(details.getOpeningHours());
        
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserLightResponse getUserLightInfo(User user) {
        if (user == null) {
            log.error("getUserLightInfo: AuthenticationPrincipal is null");
            throw new RuntimeException("Unauthorized");
        }
        
        // Re-fetch user to ensure we are in session and have latest data
        User finalUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.debug("Service: Getting light info for user {}", finalUser.getEmail());
        
        // Explicitly initialize collections if needed, though EAGER on Card should handle invitedEmails
        if (finalUser.getCard() != null) {
            finalUser.getCard().getInvitedEmails().size();
            finalUser.getCard().getMembers().size();
        }
        if (finalUser.getReferrals() != null) finalUser.getReferrals().size();
        if (finalUser.getFavorites() != null) finalUser.getFavorites().size();
        if (finalUser.getPayments() != null) finalUser.getPayments().size();
        
        return UserLightResponse.builder()
                .firstName(finalUser.getFirstName())
                .lastName(finalUser.getLastName())
                .isMember(finalUser.getSubscriptionPlan() != null)
                .card(finalUser.getCard())
                .isCardActive(finalUser.getCard() != null && finalUser.getSubscriptionPlan() != null)
                .referralCount(finalUser.getReferrals() != null ? finalUser.getReferrals().size() : 0)
                .favoriteCount(finalUser.getFavorites() != null ? finalUser.getFavorites().size() : 0)
                .subscriptionDate(finalUser.getSubscriptionDate())
                .renewalDate(finalUser.getRenewalDate())
                .subscriptionAmount(finalUser.getSubscriptionAmount())
                .payments(finalUser.getPayments())
                .build();
    }
}
