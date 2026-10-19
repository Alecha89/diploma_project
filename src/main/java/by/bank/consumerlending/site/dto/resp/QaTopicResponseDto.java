package by.bank.consumerlending.site.dto.resp;

import java.time.Instant;
import java.util.UUID;

public record QaTopicResponseDto(UUID id,
                                 String label,
                                 Integer sortOrder,
                                 Boolean isActive,
                                 Instant createdAt,
                                 Instant updatedAt) {
}