package by.bank.consumerlending.site.nbrb.client;

import by.bank.consumerlending.site.nbrb.dto.NbrbRateResponseDto;
import by.bank.consumerlending.site.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NbrbClient {

    private final CurrencyRateService currencyRateService;

    private final RestClient nbrbRestClient;

    @Async
    public void executeSync() {
        log.info("Запуск синхронизации курсов валют с НБРБ...");
        try {
            List<NbrbRateResponseDto> rates = nbrbRestClient.get()
                    .uri("/exrates/rates?periodicity=0")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (_, response) -> {
                        String errorMessage = String.format("Ошибка API НБРБ: %s", response.getStatusCode());
                        log.error(errorMessage);
                        throw new HttpClientErrorException(response.getStatusCode(), response.getStatusText());
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (rates == null || rates.isEmpty()) {
                log.warn("API НБРБ вернул пустой список курсов");
                return;
            }
            for (NbrbRateResponseDto rateDto : rates) {
                if (rateDto != null && rateDto.officialRate() != null && rateDto.abbreviation() != null) {
                    currencyRateService.updateCurrencyRate(
                            rateDto.abbreviation(),
                            rateDto.scale(),
                            rateDto.officialRate()
                    );
                }
            }
            log.info("Синхронизация курсов валют успешно завершена");
        } catch (Exception e) {
            log.error("Ошибка при синхронизации курсов от НБРБ: {}", e.getMessage());
            throw e;
        }
    }
}