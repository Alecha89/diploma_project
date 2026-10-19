package by.bank.consumerlending.site.service.impl;

import by.bank.consumerlending.site.dto.req.QaTopicCreateRequestDto;
import by.bank.consumerlending.site.dto.req.QaTopicUpdateRequestDto;
import by.bank.consumerlending.site.dto.resp.QaTopicResponseDto;
import by.bank.consumerlending.site.entity.QaTopic;
import by.bank.consumerlending.site.exception.QaTopicNotFoundException;
import by.bank.consumerlending.site.mapper.QaTopicMapper;
import by.bank.consumerlending.site.repo.QaTopicRepository;
import by.bank.consumerlending.site.service.QaTopicService;
import by.bank.consumerlending.site.util.HtmlSanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class QaTopicServiceImpl implements QaTopicService {

    private final QaTopicRepository topicRepository;

    private final QaTopicMapper topicMapper;

    @Transactional(readOnly = true)
    @Override
    public List<QaTopicResponseDto> getActiveTopics() {
        return topicRepository.findAllByIsActiveTrueAndDeletedFalseOrderBySortOrderAscLabelAsc().stream()
                .map(topicMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<QaTopicResponseDto> getAllTopics() {
        return topicRepository.findAllByDeletedFalseOrderBySortOrderAscLabelAsc().stream()
                .map(topicMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public QaTopicResponseDto createTopic(QaTopicCreateRequestDto dto) {
        log.debug("Создание новой темы: {}", dto.label());
        String sanitizedLabel = HtmlSanitizerUtil.sanitize(dto.label());

        QaTopic topic = QaTopic.builder()
                .label(sanitizedLabel)
                .sortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0)
                .isActive(dto.isActive() == null || dto.isActive())
                .build();

        QaTopic saved = topicRepository.save(topic);
        log.info("Тема с id [{}] успешно создана", saved.getId());
        return topicMapper.toDto(saved);
    }

    @Transactional
    @Override
    public QaTopicResponseDto updateTopic(UUID id, QaTopicUpdateRequestDto dto) {
        log.debug("Обновление темы с id [{}]", id);
        QaTopic topic = topicRepository.findById(id)
                .orElseThrow(() -> new QaTopicNotFoundException("Тема с id " + id + " не найдена"));

        if (dto.label() != null) topic.setLabel(dto.label());
        if (dto.sortOrder() != null) topic.setSortOrder(dto.sortOrder());
        if (dto.isActive() != null) topic.setIsActive(dto.isActive());

        QaTopic updated = topicRepository.saveAndFlush(topic);

        log.info("Тема с id [{}] обновлена", id);
        return topicMapper.toDto(updated);
    }

    @Transactional
    @Override
    public void deleteTopic(UUID id, String username) {
        log.debug("Удаление темы с id [{}] пользователем [{}]", id, username);
        QaTopic topic = topicRepository.findById(id)
                .orElseThrow(() -> new QaTopicNotFoundException("Тема с id " + id + " не найдена"));

        if (topic.isDeleted()) {
            log.warn("Тема с id [{}] уже была удалена ранее", id);
            return;
        }

        topic.setDeleted(true);
        topic.setDeletedAt(Instant.now());
        topic.setDeletedBy(username);
        topic.setIsActive(false);

        topicRepository.save(topic);
        log.info("Тема с id [{}] успешно удалена", id);
    }
}