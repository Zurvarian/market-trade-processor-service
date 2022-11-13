package org.zurvarian.markets.trade.volume;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zurvarian.markets.config.WebSecurityConfig;
import org.zurvarian.markets.trade.volume.model.TradeVolume;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.zurvarian.markets.trade.volume.TradeVolumeControllerTest.Fixture.apiRole;
import static org.zurvarian.markets.trade.volume.TradeVolumeControllerTest.Fixture.apiUser;
import static org.zurvarian.markets.trade.volume.TradeVolumeControllerTest.Fixture.listOfTradeVolumeType;
import static org.zurvarian.markets.trade.volume.TradeVolumeControllerTest.Fixture.tradeVolume;
import static reactor.core.publisher.Flux.just;

@WebFluxTest(TradeVolumeController.class)
@Import(WebSecurityConfig.class)
class TradeVolumeControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private TradeVolumeService tradeVolumeService;

    @Test
    @WithMockUser(username = apiUser, roles = apiRole)
    void given24Hours_andMatchingTradeVolumes_whenFindAllVolumes_thenVolumesAreFound() {
        given(tradeVolumeService.findAllVolumes(anyInt())).willReturn(just(tradeVolume));

        List<List<TradeVolume>> result = webClient
                .get().uri("/trades/volumes?dateTimeRangeInHours=24")
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(listOfTradeVolumeType)
                .getResponseBody()
                .take(1L)
                .collectList()
                .block();

        assertThat(result).containsExactly(singletonList(tradeVolume));
        then(tradeVolumeService).should().findAllVolumes(24);
    }

    @Test
    @WithMockUser(username = apiUser, roles = apiRole)
    void given24Hours_andNotMatchingTradeVolumes_whenFindAllVolumes_thenNoVolumesAreFound() {
        given(tradeVolumeService.findAllVolumes(anyInt())).willReturn(Flux.empty());

        List<List<TradeVolume>> result = webClient
                .get().uri("/trades/volumes?dateTimeRangeInHours=24")
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(listOfTradeVolumeType)
                .getResponseBody()
                .take(1L)
                .collectList()
                .block();

        assertThat(result).containsExactly(emptyList());
        then(tradeVolumeService).should().findAllVolumes(24);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @WithMockUser(username = apiUser, roles = apiRole)
    void givenInvalidHours_whenFindAllVolumes_thenRequestIsRejected(int invalidHours) {
        webClient
                .get().uri(String.format("/trades/volumes?dateTimeRangeInHours=%s", invalidHours))
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    interface Fixture {

        ParameterizedTypeReference<List<TradeVolume>> listOfTradeVolumeType = new ParameterizedTypeReference<>() {
        };

        String apiUser = "admin";
        String apiRole = "ADMIN";

        TradeVolume tradeVolume = TradeVolume.builder()
                .composedKey("TEST_KEY")
                .periodPoint(Instant.now())
                .currencyFrom("TEST1")
                .currencyTo("TEST2")
                .volumeCount(10)
                .build();
    }

}