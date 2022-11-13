package org.zurvarian.markets.trade.volume;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zurvarian.markets.trade.model.TradeEvent;
import org.zurvarian.markets.trade.volume.model.TradeVolume;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.zurvarian.markets.trade.volume.TradeVolumeServiceTest.Fixture.composedKey;
import static org.zurvarian.markets.trade.volume.TradeVolumeServiceTest.Fixture.currencyFrom;
import static org.zurvarian.markets.trade.volume.TradeVolumeServiceTest.Fixture.currencyTo;
import static org.zurvarian.markets.trade.volume.TradeVolumeServiceTest.Fixture.periodPoint;
import static org.zurvarian.markets.trade.volume.TradeVolumeServiceTest.Fixture.tradeEvent;
import static org.zurvarian.markets.trade.volume.TradeVolumeServiceTest.Fixture.tradeVolume;

@ExtendWith(MockitoExtension.class)
class TradeVolumeServiceTest {

    @Mock
    private TradeVolumeRepository tradeVolumeRepository;

    @InjectMocks
    private TradeVolumeService tradeVolumeService;

    @Test
    void givenTradeEvent_whenUpdateTradeVolume_thenTradeVolumeIsUpdated() {
        given(tradeVolumeRepository.updateVolumeOfTrades(anyString(), any(Instant.class), anyString(), anyString()))
                .willReturn(Mono.empty());

        Mono<Integer> result = tradeVolumeService.updateTradeVolume(tradeEvent);

        assertThat(result).isNotNull();
        then(tradeVolumeRepository).should().updateVolumeOfTrades(
                composedKey, periodPoint, currencyFrom, currencyTo
        );
    }

    @Test
    void givenSomeTradeVolumes_whenFindAllVolumes_thenTradeVolumesAreFound() {
        given(tradeVolumeRepository.findByDateTimeRangeInHoursToNow(anyInt()))
                .willReturn(Flux.just(tradeVolume));

        Flux<TradeVolume> result = tradeVolumeService.findAllVolumes(24);

        assertThat(result.toStream()).containsOnly(tradeVolume);
    }

    interface Fixture {

        String composedKey = "2022-01-24T10@@USD@@EUR";
        Instant periodPoint = Instant.parse("2022-01-24T10:00:00Z");
        String currencyFrom = "USD";
        String currencyTo = "EUR";

        TradeEvent tradeEvent = TradeEvent.builder()
                .timePlaced("24-JAN-22 10:27:44")
                .currencyFrom("USD")
                .currencyTo("EUR")
                .build();

        TradeVolume tradeVolume = TradeVolume.builder()
                .composedKey(composedKey)
                .periodPoint(periodPoint)
                .currencyFrom(currencyFrom)
                .currencyTo(currencyTo)
                .volumeCount(10)
                .build();
    }
}