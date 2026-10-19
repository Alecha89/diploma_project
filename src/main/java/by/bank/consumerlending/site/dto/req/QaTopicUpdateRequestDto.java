package by.bank.consumerlending.site.dto.req;

import jakarta.validation.constraints.AssertTrue;

public record QaTopicUpdateRequestDto(String label,
                                      Integer sortOrder,
                                      Boolean isActive) {

    @AssertTrue(message = "Должно быть заполнено хотя бы одно поле")
    @SuppressWarnings("unused")
    public boolean isAnyFieldPresent() {
        return label != null || sortOrder != null || isActive != null;
    }
}