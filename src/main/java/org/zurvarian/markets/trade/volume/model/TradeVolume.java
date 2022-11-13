package org.zurvarian.markets.trade.volume.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Value
@Builder
public class TradeVolume {

    @Id
    String composedKey;

    String currencyFrom;
    String currencyTo;
    Instant periodPoint;
    int volumeCount;
}
