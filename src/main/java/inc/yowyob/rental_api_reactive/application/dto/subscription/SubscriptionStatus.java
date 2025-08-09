package inc.yowyob.rental_api_reactive.application.dto.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SubscriptionStatus {
    @JsonProperty("ACTIVE")
    ACTIVE,

    @JsonProperty("SUSPENDED")
    SUSPENDED,

    @JsonProperty("EXPIRED")
    EXPIRED,

    @JsonProperty("CANCELLED")
    CANCELLED,

    @JsonProperty("PENDING_PAYMENT")
    PENDING_PAYMENT
}
