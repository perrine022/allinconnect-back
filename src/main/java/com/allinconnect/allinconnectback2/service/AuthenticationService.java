package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.dto.*;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import com.allinconnect.allinconnectback2.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(UserRegistrationRequest request) {
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
        
        User user = userBuilder.build();
        user.setReferralCode(personalReferralCode);

        if (request.getReferralCode() != null && !request.getReferralCode().isEmpty()) {
            userRepository.findByReferralCode(request.getReferralCode()).ifPresent(user::setReferrer);
        }
        
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    private String generateReferralCode(String lastName) {
        StringBuilder code = new StringBuilder(lastName.toLowerCase());
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        
        if (!user.isHasConnectedBefore()) {
            user.setHasConnectedBefore(true);
            userRepository.save(user);
        }
        
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public String forgotPassword(ForgotPasswordRequest request) {
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
        var user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
