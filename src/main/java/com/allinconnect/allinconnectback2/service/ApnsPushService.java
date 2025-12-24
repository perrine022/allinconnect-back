package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.DeviceToken;
import com.allinconnect.allinconnectback2.model.DeviceEnvironment;
import com.allinconnect.allinconnectback2.repository.DeviceTokenRepository;
import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

@Service
public class ApnsPushService {

    private static final Logger log = LoggerFactory.getLogger(ApnsPushService.class);

    private ApnsClient productionClient;
    private ApnsClient sandboxClient;

    @Value("${application.apns.team-id}")
    private String teamId;

    @Value("${application.apns.key-id}")
    private String keyId;

    @Value("${application.apns.bundle-id}")
    private String bundleId;

    @Value("${application.apns.p8-path}")
    private String p8Path;

    private final DeviceTokenRepository deviceTokenRepository;

    public ApnsPushService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    @PostConstruct
    public void init() {
        try {
            File p8File = new File(p8Path);
            if (!p8File.exists()) {
                log.warn("APNs p8 file not found at {}. Push service will not be available.", p8Path);
                return;
            }

            ApnsSigningKey signingKey = ApnsSigningKey.loadFromPkcs8File(p8File, teamId, keyId);

            productionClient = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                    .setSigningKey(signingKey)
                    .build();

            sandboxClient = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setSigningKey(signingKey)
                    .build();

            log.info("APNs clients initialized successfully.");
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to initialize APNs clients", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (productionClient != null) {
            productionClient.close();
        }
        if (sandboxClient != null) {
            sandboxClient.close();
        }
    }

    public void sendNewOfferNotification(DeviceToken deviceToken, Long offerId) {
        if (!deviceToken.isActive()) {
            log.debug("Skipping notification for inactive token: {}", deviceToken.getToken());
            return;
        }

        ApnsClient client = (deviceToken.getEnvironment() == DeviceEnvironment.PRODUCTION) ? productionClient : sandboxClient;

        if (client == null) {
            log.error("APNs client not initialized for environment: {}", deviceToken.getEnvironment());
            return;
        }

        String payload = "{\"aps\":{\"alert\":{\"title\":\"Nouvelle offre\",\"body\":\"Une nouvelle offre vient d’être publiée\"}},\"offerId\":" + offerId + "}";
        final String token = TokenUtil.sanitizeTokenString(deviceToken.getToken());

        final SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, bundleId, payload);

        client.sendNotification(pushNotification).whenComplete((response, cause) -> {
            if (response != null) {
                handleResponse(response, deviceToken);
            } else {
                log.error("Failed to send push notification", cause);
            }
        });
    }

    public void sendNewProfessionalNotification(DeviceToken deviceToken, Long professionalId) {
        if (!deviceToken.isActive()) {
            log.debug("Skipping notification for inactive token: {}", deviceToken.getToken());
            return;
        }

        ApnsClient client = (deviceToken.getEnvironment() == DeviceEnvironment.PRODUCTION) ? productionClient : sandboxClient;

        if (client == null) {
            log.error("APNs client not initialized for environment: {}", deviceToken.getEnvironment());
            return;
        }

        String payload = "{\"aps\":{\"alert\":{\"title\":\"Nouvel établissement\",\"body\":\"Un nouvel établissement vient de rejoindre la plateforme !\"}},\"professionalId\":" + professionalId + "}";
        final String token = TokenUtil.sanitizeTokenString(deviceToken.getToken());

        final SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, bundleId, payload);

        client.sendNotification(pushNotification).whenComplete((response, cause) -> {
            if (response != null) {
                handleResponse(response, deviceToken);
            } else {
                log.error("Failed to send push notification", cause);
            }
        });
    }

    private void handleResponse(PushNotificationResponse<SimpleApnsPushNotification> response, DeviceToken deviceToken) {
        if (response.isAccepted()) {
            log.debug("Push notification accepted by APNs for token: {}", deviceToken.getToken());
        } else {
            String reason = response.getRejectionReason().orElse("Unknown");
            log.warn("Push notification rejected by APNs: {}", reason);

            response.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
                log.warn("\t…and the token is invalid as of {}", timestamp);
            });

            // If token is invalid or unregistered, mark as inactive in DB
            if ("BadDeviceToken".equals(reason) || "Unregistered".equals(reason)) {
                log.info("Marking token {} as inactive due to rejection reason: {}", deviceToken.getToken(), reason);
                deviceToken.setActive(false);
                deviceTokenRepository.save(deviceToken);
            }
        }
    }
}
