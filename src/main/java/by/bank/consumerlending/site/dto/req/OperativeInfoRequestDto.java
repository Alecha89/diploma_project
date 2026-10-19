package by.bank.consumerlending.site.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OperativeInfoRequestDto(@NotBlank(message = "Заголовок не должен быть пустым")
                                      @Size(max = 255, message = "Заголовок не должен превышать 255 символов")
                                      String title,
                                      @NotBlank(message = "Контент не должен быть пустым")
                                      String contentHtml) {
}