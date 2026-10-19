package by.bank.consumerlending.site.controller;

import by.bank.consumerlending.site.dto.resp.CurrencyRateResponseDto;
import by.bank.consumerlending.site.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @GetMapping("/{abbreviation}")
    public ResponseEntity<CurrencyRateResponseDto> getRate(@PathVariable String abbreviation) {
        return ResponseEntity.ok(currencyRateService.getCurrencyRate(abbreviation));
    }
}
