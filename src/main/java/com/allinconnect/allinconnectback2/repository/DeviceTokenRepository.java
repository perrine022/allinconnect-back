package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.DeviceToken;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.model.DevicePlatform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByToken(String token);
    List<DeviceToken> findByUser(User user);

    @Query("SELECT d FROM DeviceToken d WHERE d.platform = :platform AND d.active = true")
    Page<DeviceToken> findActiveTokensByPlatform(@Param("platform") DevicePlatform platform, Pageable pageable);
}
