package by.bank.consumerlending.site.dto.resp;

import java.time.Instant;
import java.util.UUID;

public record OperativeInfoShortResponseDto(UUID id,
                                            String title,
                                            String contentHtml,
                                            String authorName,
                                            Instant createdAt
) {
}