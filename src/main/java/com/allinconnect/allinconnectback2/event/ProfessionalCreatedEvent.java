package com.allinconnect.allinconnectback2.event;

public class ProfessionalCreatedEvent {
    private final Long professionalId;

    public ProfessionalCreatedEvent(Long professionalId) {
        this.professionalId = professionalId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }
}
