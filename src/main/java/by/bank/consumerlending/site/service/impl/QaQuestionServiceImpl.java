package by.bank.consumerlending.site.service.impl;

import by.bank.consumerlending.site.dto.req.QaAnswerRequestDto;
import by.bank.consumerlending.site.dto.req.QaQuestionRequestDto;
import by.bank.consumerlending.site.dto.resp.PagedListResponseDto;
import by.bank.consumerlending.site.dto.resp.QaQuestionResponseDto;
import by.bank.consumerlending.site.entity.QaQuestion;
import by.bank.consumerlending.site.entity.QaStatus;
import by.bank.consumerlending.site.entity.QaTopic;
import by.bank.consumerlending.site.entity.specification.QaQuestionSpecification;
import by.bank.consumerlending.site.exception.QaInactiveTopicException;
import by.bank.consumerlending.site.exception.QaQuestionAlreadyAnsweredException;
import by.bank.consumerlending.site.exception.QaQuestionNotFoundException;
import by.bank.consumerlending.site.exception.QaTooManyQuestionsException;
import by.bank.consumerlending.site.exception.QaTopicNotFoundException;
import by.bank.consumerlending.site.mapper.QaQuestionMapper;
import by.bank.consumerlending.site.repo.QaQuestionRepository;
import by.bank.consumerlending.site.repo.QaTopicRepository;
import by.bank.consumerlending.site.service.QaQuestionService;
import by.bank.consumerlending.site.util.HtmlSanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class QaQuestionServiceImpl implements QaQuestionService {

    private final QaTopicRepository topicRepository;

    private final QaQuestionRepository questionRepository;

    private final QaQuestionMapper questionMapper;

    @Transactional(readOnly = true)
    @Override
    public PagedListResponseDto<QaQuestionResponseDto> getQuestions(QaStatus status, UUID topicId, int page, int pageSize) {
        log.debug("Запрос списка вопросов. Фильтры: status={}, topicId={}", status, topicId);
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<QaQuestion> spec = Specification.where(QaQuestionSpecification.specificationIsNotDeleted())
                .and(QaQuestionSpecification.specificationByStatus(status))
                .and(QaQuestionSpecification.specificationByTopicId(topicId));

        Page<QaQuestion> pageResult = questionRepository.findAll(spec, pageable);

        return new PagedListResponseDto<>(
                pageResult.getContent().stream().map(questionMapper::toDto).toList(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<QaQuestionResponseDto> getMyQuestions(String username) {
        log.debug("Запрос вопросов текущего пользователя [{}]", username);

        long totalQuestions = questionRepository.countByAuthorNameAndTopicIsActiveTrueAndTopicDeletedFalse(username);
        if (totalQuestions > 100) {
            throw new QaTooManyQuestionsException("Слишком много записей: " + totalQuestions + ". Пожалуйста, используйте пагинацию.");
        }
        List<QaQuestion> questions = questionRepository
                .findAllByAuthorNameAndTopicIsActiveTrueAndTopicDeletedFalseOrderByCreatedAtDesc(username);

        return questions.stream()
                .map(questionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public QaQuestionResponseDto getQuestionById(UUID id) {
        log.debug("Запрос вопроса по id [{}]", id);
        QaQuestion question = questionRepository.findWithTopicByIdAndTopicDeletedFalse(id)
                .orElseThrow(() -> new QaQuestionNotFoundException("Вопрос с id " + id + " не найден"));

        return questionMapper.toDto(question);
    }

    @Transactional
    @Override
    public QaQuestionResponseDto createQuestion(QaQuestionRequestDto dto, String username) {
        log.debug("Создание нового вопроса для темы [{}]", dto.topicId());
        QaTopic topic = topicRepository.findById(dto.topicId())
                .orElseThrow(() -> new QaTopicNotFoundException("Тема с id " + dto.topicId() + " не найдена"));

        if (!topic.getIsActive() || topic.isDeleted()) {
            throw new QaInactiveTopicException("Невозможно создать вопрос: тема с id " + topic.getId() + " неактивна");
        }
        String sanitizedQuestionText = HtmlSanitizerUtil.sanitize(dto.questionText());

        QaQuestion question = QaQuestion.builder()
                .topic(topic)
                .authorName(username)
                .questionText(sanitizedQuestionText)
                .status(QaStatus.PENDING)
                .build();

        QaQuestion saved = questionRepository.save(question);
        log.info("Вопрос с id [{}] успешно создан со статусом PENDING", saved.getId());
        return questionMapper.toDto(saved);
    }

    @Transactional
    @Override
    public QaQuestionResponseDto answerQuestion(UUID id, QaAnswerRequestDto dto, String username) {
        log.debug("Публикация ответа на вопрос [{}]", id);

        QaQuestion question = questionRepository.findWithTopicByIdAndTopicDeletedFalse(id)
                .orElseThrow(() -> new QaQuestionNotFoundException("Вопрос с id " + id + " не найден"));

        if (!QaStatus.PENDING.equals(question.getStatus())) {
            throw new QaQuestionAlreadyAnsweredException("Невозможно ответить на вопрос с id " + id +
                    ". Текущий статус: " + question.getStatus());
        }
        String sanitizedAnswerText = HtmlSanitizerUtil.sanitize(dto.answerText());

        question.setAnswerText(sanitizedAnswerText);
        question.setStatus(QaStatus.ANSWERED);
        question.setAnsweredAt(Instant.now());
        question.setAnsweredByName(username);

        log.info("На вопрос [{}] опубликован ответ. Статус изменен на ANSWERED", id);
        return questionMapper.toDto(question);
    }
}