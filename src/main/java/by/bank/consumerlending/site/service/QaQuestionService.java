package by.bank.consumerlending.site.service;

import by.bank.consumerlending.site.dto.req.QaAnswerRequestDto;
import by.bank.consumerlending.site.dto.req.QaQuestionRequestDto;
import by.bank.consumerlending.site.dto.resp.PagedListResponseDto;
import by.bank.consumerlending.site.dto.resp.QaQuestionResponseDto;
import by.bank.consumerlending.site.entity.QaStatus;

import java.util.List;
import java.util.UUID;

public interface QaQuestionService {

    PagedListResponseDto<QaQuestionResponseDto> getQuestions(QaStatus status, UUID topicId, int page, int pageSize);

    List<QaQuestionResponseDto> getMyQuestions(String tbn);

    QaQuestionResponseDto getQuestionById(UUID id);

    QaQuestionResponseDto createQuestion(QaQuestionRequestDto dto, String username);

    QaQuestionResponseDto answerQuestion(UUID id, QaAnswerRequestDto dto, String username);
}