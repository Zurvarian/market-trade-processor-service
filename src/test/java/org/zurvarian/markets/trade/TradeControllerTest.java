package org.zurvarian.markets.trade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zurvarian.markets.config.WebSecurityConfig;
import org.zurvarian.markets.trade.model.TradeEvent;
import org.zurvarian.markets.trade.volume.TradeVolumeService;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zurvarian.markets.trade.TradeControllerTest.Fixture.apiRole;
import static org.zurvarian.markets.trade.TradeControllerTest.Fixture.apiUser;
import static org.zurvarian.markets.trade.TradeControllerTest.Fixture.tradeEvent;

@WebFluxTest(TradeController.class)
@Import(WebSecurityConfig.class)
class TradeControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private TradeVolumeService tradeVolumeService;

    @Test
    @WithMockUser(username = apiUser, roles = apiRole)
    void given24Hours_andMatchingTradeVolumes_whenFindAllVolumes_thenVolumesAreFound() {
        given(tradeVolumeService.updateTradeVolume(any(TradeEvent.class))).willReturn(Mono.empty());

        webClient
                .post().uri("/trades")
                .contentType(APPLICATION_JSON)
                .bodyValue(tradeEvent)
                .exchange()
                .expectStatus()
                .isAccepted();

        then(tradeVolumeService).should().updateTradeVolume(tradeEvent);
    }

    interface Fixture {

        String apiUser = "test";
        String apiRole = "ADMIN";

        TradeEvent tradeEvent = TradeEvent.builder().build();
    }

}
