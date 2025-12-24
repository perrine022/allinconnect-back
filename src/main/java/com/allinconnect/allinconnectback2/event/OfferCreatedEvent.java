package com.allinconnect.allinconnectback2.event;

public class OfferCreatedEvent {
    private final Long offerId;

    public OfferCreatedEvent(Long offerId) {
        this.offerId = offerId;
    }

    public Long getOfferId() {
        return offerId;
    }
}
