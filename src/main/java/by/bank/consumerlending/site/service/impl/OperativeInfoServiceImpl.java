package by.bank.consumerlending.site.service.impl;

import by.bank.consumerlending.site.dto.req.OperativeInfoRequestDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoResponseDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoShortResponseDto;
import by.bank.consumerlending.site.dto.resp.PagedListResponseDto;
import by.bank.consumerlending.site.entity.OperativeInfo;
import by.bank.consumerlending.site.exception.OperativeInfoNotFoundException;
import by.bank.consumerlending.site.mapper.OperativeInfoMapper;
import by.bank.consumerlending.site.repo.OperativeInfoRepository;
import by.bank.consumerlending.site.service.OperativeInfoService;
import by.bank.consumerlending.site.util.HtmlSanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class OperativeInfoServiceImpl implements OperativeInfoService {

    private final OperativeInfoRepository operativeInfoRepository;

    private final OperativeInfoMapper operativeInfoMapper;

    @Transactional(readOnly = true)
    @Override
    public PagedListResponseDto<OperativeInfoResponseDto> getAllOperativeInfo(int page, int pageSize) {
        log.debug("Запрос списка оперативной информации: page={}, pageSize={}", page, pageSize);

        int springPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(
                springPage, pageSize, Sort.by("createdAt", "id").descending());

        Page<OperativeInfo> pageContent = operativeInfoRepository.findAll(pageable);

        List<OperativeInfoResponseDto> content = pageContent.getContent().stream()
                .map(operativeInfoMapper::toDto)
                .toList();

        return new PagedListResponseDto<>(content, pageContent.getTotalElements(), page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OperativeInfoShortResponseDto> getLatestOperativeInfo(int limit) {
        log.debug("Запрос списка последней оперативной информации: limit={}", limit);
        List<OperativeInfo> latest = operativeInfoRepository.findBy(
                Limit.of(limit),
                Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        return latest.stream()
                .map(operativeInfoMapper::toShortDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public OperativeInfoResponseDto getOperativeInfoById(UUID id) {
        log.debug("Получение оперативной информации с id [{}]", id);
        return operativeInfoRepository.findById(id)
                .map(operativeInfoMapper::toDto)
                .orElseThrow(() -> new OperativeInfoNotFoundException("Оперативная информация с id " + id + " не найдена"));
    }

    @Transactional
    @Override
    public OperativeInfoResponseDto createOperativeInfo(OperativeInfoRequestDto dto, String username) {
        log.debug("Создание новой оперативной информации с названием [{}] пользователем [{}]", dto.title(), username);

        String finalHtml = HtmlSanitizerUtil.sanitize(dto.contentHtml());

        OperativeInfo operativeInfo = operativeInfoMapper.toEntity(dto);
        operativeInfo.setContentHtml(finalHtml);
        operativeInfo.setAuthorName(username);

        OperativeInfo saved = operativeInfoRepository.save(operativeInfo);
        log.info("Оперативная информация сохранена с id [{}]", saved.getId());
        return operativeInfoMapper.toDto(saved);
    }

    @Transactional
    @Override
    public OperativeInfoResponseDto updateOperativeInfo(UUID id, OperativeInfoRequestDto dto) {
        log.debug("Обновление оперативной информации с id [{}]", id);
        OperativeInfo operativeInfo = operativeInfoRepository.findById(id)
                .orElseThrow(() -> new OperativeInfoNotFoundException("Оперативная информация с id " + id + " не найдена"));

        operativeInfoMapper.updateEntityFromDto(dto, operativeInfo);
        String finalHtml = HtmlSanitizerUtil.sanitize(dto.contentHtml());
        operativeInfo.setContentHtml(finalHtml);

        OperativeInfo updated = operativeInfoRepository.save(operativeInfo);
        log.info("Оперативная информация с id [{}] обновлена", updated.getId());
        return operativeInfoMapper.toDto(updated);
    }

    @Transactional
    @Override
    public void deleteOperativeInfo(UUID id) {
        log.debug("Удаление оперативной информации с id [{}]", id);
        OperativeInfo operativeInfo = operativeInfoRepository.findById(id)
                .orElseThrow(() -> new OperativeInfoNotFoundException("Оперативная информация с id " + id + " не найдена"));
        operativeInfoRepository.delete(operativeInfo);
    }
}