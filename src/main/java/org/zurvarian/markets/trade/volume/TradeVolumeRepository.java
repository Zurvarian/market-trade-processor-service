package org.zurvarian.markets.trade.volume;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.zurvarian.markets.trade.volume.model.TradeVolume;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface TradeVolumeRepository extends R2dbcRepository<TradeVolume, String> {

    @Modifying
    @Query("""
                MERGE INTO trade_volume AS t USING (VALUES (:composedKey, :periodPoint, :currencyFrom, :currencyTo)) AS s (composed_key, period_point, currency_from, currency_to)
                ON t.composed_key = s.composed_key
                WHEN MATCHED THEN
                    UPDATE SET t.volume_count = t.volume_count + 1
                WHEN NOT MATCHED THEN
                    INSERT (composed_key, period_point, currency_from, currency_to, volume_count) 
                    VALUES (s.composed_key, s.period_point, s.currency_from, s.currency_to, 1)
            """)
    Mono<Integer> updateVolumeOfTrades(String composedKey, Instant periodPoint, String currencyFrom, String currencyTo);

    @Query("""
                SELECT *
                FROM trade_volume AS t
                WHERE t.period_point >= DATEADD(HOUR, -:dateTimeRangeInHours, CURRENT_TIMESTAMP())
                ORDER BY t.period_point ASC
            """)
    Flux<TradeVolume> findByDateTimeRangeInHoursToNow(Integer dateTimeRangeInHours);
}
