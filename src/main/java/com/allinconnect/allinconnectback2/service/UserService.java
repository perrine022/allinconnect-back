package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.dto.ChangePasswordRequest;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.UserType;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public List<User> findProfessionalsByCity(String city) {
        return userRepository.findByUserTypeAndCity(UserType.PROFESSIONAL, city);
    }

    public List<User> searchProfessionals(String city, ProfessionCategory category) {
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
        User favorite = userRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite user not found"));
        
        if (user.getFavorites().contains(favorite)) {
            throw new RuntimeException("User already in favorites");
        }
        
        user.getFavorites().add(favorite);
        userRepository.save(user);
    }

    public void removeFavorite(User user, Long favoriteId) {
        User favorite = userRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite user not found"));
        
        user.getFavorites().remove(favorite);
        userRepository.save(user);
    }

    public List<User> getFavorites(User user) {
        return user.getFavorites();
    }
}
