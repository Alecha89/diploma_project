package by.bank.consumerlending.site.dto.req;

import jakarta.validation.constraints.NotBlank;

public record QaTopicCreateRequestDto(@NotBlank(message = "Название темы не должно быть пустым")
                                      String label,
                                      Integer sortOrder,
                                      Boolean isActive) {
}