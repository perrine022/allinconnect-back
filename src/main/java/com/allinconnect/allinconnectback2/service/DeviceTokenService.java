package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.dto.DeviceTokenRequest;
import com.allinconnect.allinconnectback2.entity.DeviceToken;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.repository.DeviceTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeviceTokenService {

    private static final Logger log = LoggerFactory.getLogger(DeviceTokenService.class);
    private final DeviceTokenRepository deviceTokenRepository;

    public DeviceTokenService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    public DeviceToken registerToken(DeviceTokenRequest request, User user) {
        log.debug("Registering device token for user {}", user.getEmail());
        
        DeviceToken deviceToken = deviceTokenRepository.findByToken(request.getToken())
                .orElse(new DeviceToken());
        
        deviceToken.setToken(request.getToken());
        deviceToken.setPlatform(request.getPlatform());
        deviceToken.setEnvironment(request.getEnvironment());
        deviceToken.setUser(user);
        
        return deviceTokenRepository.save(deviceToken);
    }
}
