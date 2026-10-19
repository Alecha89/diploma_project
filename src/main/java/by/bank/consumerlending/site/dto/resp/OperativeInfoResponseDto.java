package by.bank.consumerlending.site.dto.resp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OperativeInfoResponseDto(UUID id,
                                       String title,
                                       String contentHtml,
                                       String authorName,
                                       Instant createdAt
) {
}