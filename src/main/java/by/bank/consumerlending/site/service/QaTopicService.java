package by.bank.consumerlending.site.service;

import by.bank.consumerlending.site.dto.req.QaTopicCreateRequestDto;
import by.bank.consumerlending.site.dto.req.QaTopicUpdateRequestDto;
import by.bank.consumerlending.site.dto.resp.QaTopicResponseDto;

import java.util.List;
import java.util.UUID;

public interface QaTopicService {

    List<QaTopicResponseDto> getActiveTopics();

    List<QaTopicResponseDto> getAllTopics();

    QaTopicResponseDto createTopic(QaTopicCreateRequestDto dto);

    QaTopicResponseDto updateTopic(UUID id, QaTopicUpdateRequestDto dto);

    void deleteTopic(UUID id, String tbn);
}