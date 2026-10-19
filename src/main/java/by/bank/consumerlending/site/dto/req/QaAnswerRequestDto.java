package by.bank.consumerlending.site.dto.req;

import jakarta.validation.constraints.NotBlank;

public record QaAnswerRequestDto(@NotBlank(message = "Текст ответа не может быть пустым")
                                 String answerText) {
}