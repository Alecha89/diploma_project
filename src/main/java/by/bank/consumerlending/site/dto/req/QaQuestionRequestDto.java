package by.bank.consumerlending.site.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QaQuestionRequestDto(@NotNull(message = "Идентификатор темы обязателен")
                                   UUID topicId,
                                   @NotBlank(message = "Текст вопроса не может быть пустым")
                                   String questionText) {
}