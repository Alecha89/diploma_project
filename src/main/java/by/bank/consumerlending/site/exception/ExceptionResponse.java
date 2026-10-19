package by.bank.consumerlending.site.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

public record ExceptionResponse(String code,
                                String message,
                                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                                Map<String, List<String>> details) {
}
