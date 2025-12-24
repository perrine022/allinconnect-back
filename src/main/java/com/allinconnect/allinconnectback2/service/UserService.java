package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.dto.ChangePasswordRequest;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.UserType;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(User user, ChangePasswordRequest request) {
        log.debug("Service: Changing password for user {}", user.getEmail());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.debug("Invalid old password for user {}", user.getEmail());
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public List<User> findProfessionalsByCity(String city) {
        log.debug("Service: Finding professionals in city {}", city);
        return userRepository.findByUserTypeAndCity(UserType.PROFESSIONAL, city);
    }

    public List<User> searchProfessionals(String city, ProfessionCategory category) {
        log.debug("Service: Searching professionals with city: {} and category: {}", city, category);
        if (city != null && category != null) {
            return userRepository.findByUserTypeAndCityAndCategory(UserType.PROFESSIONAL, city, category);
        } else if (city != null) {
            return userRepository.findByUserTypeAndCity(UserType.PROFESSIONAL, city);
        } else if (category != null) {
            return userRepository.findByUserTypeAndCategory(UserType.PROFESSIONAL, category);
        } else {
            return userRepository.findAll().stream()
                    .filter(user -> user.getUserType() == UserType.PROFESSIONAL)
                    .toList();
        }
    }

    public void addFavorite(User user, Long favoriteId) {
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
        log.debug("Service: Removing favorite {} from user {}", favoriteId, user.getEmail());
        User favorite = userRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite user not found"));
        
        user.getFavorites().remove(favorite);
        userRepository.save(user);
    }

    public List<User> getFavorites(User user) {
        log.debug("Service: Getting favorites for user {}", user.getEmail());
        return user.getFavorites();
    }
}
