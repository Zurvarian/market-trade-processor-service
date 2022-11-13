package org.zurvarian.markets.trade.volume;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zurvarian.markets.trade.volume.model.TradeVolume;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.composedKeyNewer;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.composedKeyOlder;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.currencyFrom;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.currencyTo;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.periodPointNewer;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.periodPointOlder;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.tradeVolumeNewer;
import static org.zurvarian.markets.trade.volume.TradeVolumeRepositoryTest.Fixture.tradeVolumeOlder;

@SpringBootTest
class TradeVolumeRepositoryTest {

    @Autowired
    private TradeVolumeRepository tradeVolumeRepository;

    @BeforeEach
    void setUp() {
        tradeVolumeRepository.deleteAll().block();
    }

    @Test
    void givenMissingTradeVolume_whenUpdateVolumeOfTrades_thenTradeVolumeIsCreated() {
        Mono<Integer> result = tradeVolumeRepository.updateVolumeOfTrades(composedKeyOlder, periodPointOlder, currencyFrom, currencyTo);

        Integer updatedEntries = result.block();
        assertThat(updatedEntries).isEqualTo(1);

        Optional<TradeVolume> tradeVolumeCreated = tradeVolumeRepository.findById(composedKeyOlder).blockOptional();
        assertThat(tradeVolumeCreated).contains(tradeVolumeOlder);
    }

    @Test
    void givenExistingTradeVolume_whenUpdateVolumeOfTrades_thenTradeVolumeIsCreated() {
        tradeVolumeRepository.updateVolumeOfTrades(composedKeyNewer, periodPointNewer, currencyFrom, currencyTo).block();
        tradeVolumeRepository.updateVolumeOfTrades(composedKeyNewer, periodPointNewer, currencyFrom, currencyTo).block();

        Optional<TradeVolume> tradeVolumeCreated = tradeVolumeRepository.findById(composedKeyNewer).blockOptional();
        assertThat(tradeVolumeCreated).contains(tradeVolumeNewer);
    }

    @Test
    void givenMissingTradesForPeriod_whenFindByDateTimeRangeInHoursToNow_thenNoTradesAreFound() {
        Flux<TradeVolume> result = tradeVolumeRepository.findByDateTimeRangeInHoursToNow(1);

        assertThat(result.toStream()).isEmpty();
    }

    @Test
    void givenExistingTradesForPeriod_whenFindByDateTimeRangeInHoursToNow_thenAllTradesAreFound() {
        tradeVolumeRepository.updateVolumeOfTrades(composedKeyOlder, periodPointOlder, currencyFrom, currencyTo).block();
        tradeVolumeRepository.updateVolumeOfTrades(composedKeyNewer, periodPointNewer, currencyFrom, currencyTo).block();
        tradeVolumeRepository.updateVolumeOfTrades(composedKeyNewer, periodPointNewer, currencyFrom, currencyTo).block();

        Flux<TradeVolume> result = tradeVolumeRepository.findByDateTimeRangeInHoursToNow(24);

        assertThat(result.toStream()).containsExactly(tradeVolumeOlder, tradeVolumeNewer);
    }

    interface Fixture {
        Instant periodPointBase = Instant.now().truncatedTo(MILLIS);
        String currencyFrom = "USD";
        String currencyTo = "EUR";

        String composedKeyOlder = "test-key-older";
        Instant periodPointOlder = periodPointBase.minus(6, HOURS);
        TradeVolume tradeVolumeOlder = TradeVolume.builder()
                .composedKey(composedKeyOlder)
                .periodPoint(periodPointOlder)
                .currencyFrom(currencyFrom)
                .currencyTo(currencyTo)
                .volumeCount(1)
                .build();

        String composedKeyNewer = "test-key-newer";
        Instant periodPointNewer = periodPointBase.minus(3, HOURS);
        TradeVolume tradeVolumeNewer = TradeVolume.builder()
                .composedKey(composedKeyNewer)
                .periodPoint(periodPointNewer)
                .currencyFrom(currencyFrom)
                .currencyTo(currencyTo)
                .volumeCount(2)
                .build();
    }
}