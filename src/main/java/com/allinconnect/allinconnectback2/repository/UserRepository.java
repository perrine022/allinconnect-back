package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.ProfessionCategory;
import com.allinconnect.allinconnectback2.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    List<User> findByUserTypeAndCity(UserType userType, String city);
    List<User> findByUserTypeAndCategory(UserType userType, ProfessionCategory category);
    List<User> findByUserTypeAndCityAndCategory(UserType userType, String city, ProfessionCategory category);
    Optional<User> findByReferralCode(String referralCode);
}
