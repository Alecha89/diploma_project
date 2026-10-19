package by.bank.consumerlending.site.service.impl;

import by.bank.consumerlending.site.dto.resp.CurrencyRateResponseDto;
import by.bank.consumerlending.site.entity.CurrencyRate;
import by.bank.consumerlending.site.exception.CurrencyRateNotFoundException;
import by.bank.consumerlending.site.repo.CurrencyRateRepository;
import by.bank.consumerlending.site.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private final CurrencyRateRepository currencyRateRepository;

    @Transactional(readOnly = true)
    @Override
    public CurrencyRateResponseDto getCurrencyRate(String abbreviation) {
        CurrencyRate currencyRate = currencyRateRepository.findById(abbreviation)
                .orElseThrow(() -> new CurrencyRateNotFoundException("Курс валюты " + abbreviation + " не найден"));
        String rate = currencyRate.getRate().toPlainString().replace('.', ',');
        return new CurrencyRateResponseDto(rate);
    }

    @Transactional
    @Override
    public void updateCurrencyRate(String abbreviation, Integer scale, BigDecimal newRate) {
        CurrencyRate rate = currencyRateRepository.findById(abbreviation)
                .orElseGet(() -> {
                    log.info("Курс для {} не найден, создание новой записи", abbreviation);
                    return new CurrencyRate(abbreviation, scale, newRate, Instant.now());
                });

        rate.setRate(newRate);
        rate.setScale(scale);
        if (rate.getUpdatedAt() == null || !currencyRateRepository.existsById(abbreviation)) {
            currencyRateRepository.save(rate);
        }
        log.info("Курс {} сохранен в БД: {}", abbreviation, newRate);
    }
}