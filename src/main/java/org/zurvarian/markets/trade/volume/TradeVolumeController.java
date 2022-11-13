package org.zurvarian.markets.trade.volume;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zurvarian.markets.trade.volume.model.TradeVolume;
import reactor.core.publisher.Flux;

import javax.validation.constraints.Min;
import java.util.List;

import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;
import static reactor.core.publisher.Flux.interval;
import static reactor.core.scheduler.Schedulers.boundedElastic;

@RestController
@RequestMapping("/trades/volumes")
@RequiredArgsConstructor
@Validated
public class TradeVolumeController {

    private final TradeVolumeService tradeVolumeService;

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    Flux<List<TradeVolume>> findAllVolumes(@RequestParam("dateTimeRangeInHours") @Min(1) Integer dateTimeRangeInHours) {
        return interval(ZERO, ofSeconds(5))
                .publishOn(boundedElastic())
                .map(
                        __ -> tradeVolumeService.findAllVolumes(dateTimeRangeInHours).toStream().collect(toList())
                );
    }

}
