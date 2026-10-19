package by.bank.consumerlending.site.dto.resp;

import by.bank.consumerlending.site.entity.QaStatus;

import java.time.Instant;
import java.util.UUID;

public record QaQuestionResponseDto(UUID id,
                                    UUID topicId,
                                    String topicLabel,
                                    String authorName,
                                    String questionText,
                                    String answerText,
                                    QaStatus status,
                                    Instant createdAt,
                                    Instant answeredAt,
                                    String answeredByName) {
}