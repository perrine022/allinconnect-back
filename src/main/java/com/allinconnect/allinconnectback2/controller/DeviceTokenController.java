package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.dto.DeviceTokenRequest;
import com.allinconnect.allinconnectback2.entity.DeviceToken;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.service.DeviceTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/push")
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    public DeviceTokenController(DeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceToken> registerToken(
            @RequestBody DeviceTokenRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(deviceTokenService.registerToken(request, user));
    }
}
