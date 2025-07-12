package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentMethod {
    @JsonProperty("MTN_MONEY")
    MTN_MONEY,

    @JsonProperty("ORANGE_MONEY")
    ORANGE_MONEY,

    @JsonProperty("CARD")
    CARD,

    @JsonProperty("BANK_TRANSFER")
    BANK_TRANSFER,

    @JsonProperty("CASH")
    CASH
}
