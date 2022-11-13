package org.zurvarian.markets.trade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.ZoneOffset.UTC;

@Value
@Builder
@Jacksonized
public class TradeEvent {
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("dd-MMM-yy HH:mm:ss")
            .toFormatter()
            .withZone(UTC);

    String userId;
    String currencyFrom;
    String currencyTo;
    BigDecimal amountSell;
    BigDecimal amountBuy;
    BigDecimal rate;
    String timePlaced;
    String originatingCountry;

    @JsonIgnore
    public Instant getTimePlacedAsInstant() {
        return timePlaced == null || timePlaced.isEmpty() ? null : Instant.from(formatter.parse(timePlaced));
    }
}
