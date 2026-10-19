package by.bank.consumerlending.site.service;

import by.bank.consumerlending.site.dto.resp.CurrencyRateResponseDto;

import java.math.BigDecimal;

public interface CurrencyRateService {

    CurrencyRateResponseDto getCurrencyRate(String abbreviation);

    void updateCurrencyRate(String abbreviation, Integer scale, BigDecimal newRate);
}