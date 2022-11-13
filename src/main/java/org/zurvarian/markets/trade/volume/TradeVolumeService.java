package org.zurvarian.markets.trade.volume;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zurvarian.markets.trade.model.TradeEvent;
import org.zurvarian.markets.trade.volume.model.TradeVolume;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.HOURS;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeVolumeService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    private final TradeVolumeRepository tradeVolumeRepository;

    public Mono<Integer> updateTradeVolume(TradeEvent tradeEvent) {

        Instant hourlyPeriodPoint =
                tradeEvent.getTimePlacedAsInstant().atZone(UTC).truncatedTo(HOURS).toInstant();
        String tradeVolumeKey = createTradeVolumeKey(
                tradeEvent.getCurrencyFrom(),
                tradeEvent.getCurrencyTo(),
                hourlyPeriodPoint
        );

        return tradeVolumeRepository.updateVolumeOfTrades(
                tradeVolumeKey,
                hourlyPeriodPoint,
                tradeEvent.getCurrencyFrom(),
                tradeEvent.getCurrencyTo()
        );
    }

    public Flux<TradeVolume> findAllVolumes(Integer dateTimeRangeInHours) {
        return tradeVolumeRepository.findByDateTimeRangeInHoursToNow(dateTimeRangeInHours);
    }

    private String createTradeVolumeKey(String currencyFrom, String currencyTo, Instant periodTime) {
        return new StringJoiner("@@")
                .add(formatter.format(periodTime.atZone(UTC)))
                .add(currencyFrom)
                .add(currencyTo)
                .toString();
    }
}
