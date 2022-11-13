package org.zurvarian.markets.trades.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.zurvarian.markets.trade.model.TradeEvent;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TradeEventTest {

    @ParameterizedTest
    @MethodSource
    void givenPlacedDate_whenGetTimePlacedAsInstant_thenResultIsExpected(String timePlaced, Instant expected) {
        TradeEvent tradeEvent = TradeEvent.builder().timePlaced(timePlaced).build();

        assertThat(tradeEvent.getTimePlacedAsInstant()).isEqualTo(expected);
    }

    private static Stream<Arguments> givenPlacedDate_whenGetTimePlacedAsInstant_thenResultIsExpected() {
        return Stream.of(
                Arguments.of("24-JAN-22 10:27:44", Instant.parse("2022-01-24T10:27:44Z")),
                Arguments.of("", null),
                Arguments.of(null, null)
        );
    }
}