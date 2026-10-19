package by.bank.consumerlending.site.nbrb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record NbrbRateResponseDto(@JsonProperty("Cur_ID") int curId,
                                  @JsonProperty("Cur_Abbreviation") String abbreviation,
                                  @JsonProperty("Cur_OfficialRate") BigDecimal officialRate,
                                  @JsonProperty("Cur_Scale") int scale) {
}