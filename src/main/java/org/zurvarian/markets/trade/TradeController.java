package org.zurvarian.markets.trade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zurvarian.markets.trade.model.TradeEvent;
import org.zurvarian.markets.trade.volume.TradeVolumeService;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
@Slf4j
public class TradeController {

    private final TradeVolumeService tradeVolumeService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(value = ACCEPTED)
    @PreAuthorize("hasRole('ADMIN')")
    Mono<Void> acceptTradeEvent(@RequestBody TradeEvent tradeEvent) {
        log.info("Received event: {}", tradeEvent);

        return tradeVolumeService.updateTradeVolume(tradeEvent)
                .then();
    }

}
