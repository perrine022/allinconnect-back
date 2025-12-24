package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.DeviceToken;
import com.allinconnect.allinconnectback2.event.OfferCreatedEvent;
import com.allinconnect.allinconnectback2.model.DevicePlatform;
import com.allinconnect.allinconnectback2.repository.DeviceTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class OfferPushNotifier {

    private static final Logger log = LoggerFactory.getLogger(OfferPushNotifier.class);
    private static final int PAGE_SIZE = 1000;
    private static final int MAX_CONCURRENT = 50;

    private final ExecutorService executorService;
    private final DeviceTokenRepository deviceTokenRepository;
    private final ApnsPushService apnsService;
    private final MetricsLogger metricsLogger;

    public OfferPushNotifier(
            DeviceTokenRepository deviceTokenRepository,
            ApnsPushService apnsService,
            MetricsLogger metricsLogger
    ) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.apnsService = apnsService;
        this.metricsLogger = metricsLogger;
        this.executorService = Executors.newFixedThreadPool(MAX_CONCURRENT);
    }

    @EventListener
    public void onOfferCreated(OfferCreatedEvent event) {
        log.info("Event received: OfferCreatedEvent for offerId {}", event.getOfferId());
        notifyOfferCreated(event.getOfferId());
    }

    public void notifyOfferCreated(Long offerId) {
        CompletableFuture.runAsync(() -> {
            try {
                int page = 0;
                Page<DeviceToken> tokenPage;

                do {
                    tokenPage = deviceTokenRepository.findActiveTokensByPlatform(
                            DevicePlatform.IOS,
                            PageRequest.of(page, PAGE_SIZE)
                    );

                    List<DeviceToken> tokens = tokenPage.getContent();
                    if (!tokens.isEmpty()) {
                        sendNotificationsInBatches(tokens, offerId);
                    }

                    page++;
                } while (tokenPage.hasNext());

                metricsLogger.log("offer_notifications_sent",
                        Map.of("offerId", offerId, "totalPages", page));

            } catch (Exception e) {
                metricsLogger.logError("offer_notification_failed",
                        Map.of("offerId", offerId), e);
            }
        }, executorService);
    }

    private void sendNotificationsInBatches(List<DeviceToken> tokens, Long offerId) {
        List<CompletableFuture<Void>> futures = tokens.stream()
                .map(token -> CompletableFuture.runAsync(() ->
                        sendWithRetry(token, offerId), executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();
    }

    private void sendWithRetry(DeviceToken token, Long offerId) {
        int maxRetries = 3;
        long baseDelayMs = 1000; // 1 second

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                // Note: apnsService.sendNewOfferNotification handles internally the async call to APNs via Pushy
                apnsService.sendNewOfferNotification(token, offerId);

                metricsLogger.log("push_notification_initiated",
                        Map.of("tokenId", token.getId(), "offerId", offerId));
                return;

            } catch (Exception e) {
                if (attempt < maxRetries - 1) {
                    long delay = baseDelayMs * (long) Math.pow(2, attempt);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } else {
                    metricsLogger.logError("push_notification_failed_after_retries",
                            Map.of("tokenId", token.getId(), "offerId", offerId), e);
                }
            }
        }
    }
}
