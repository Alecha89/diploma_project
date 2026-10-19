package by.bank.consumerlending.site.service;

import by.bank.consumerlending.site.dto.req.OperativeInfoRequestDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoResponseDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoShortResponseDto;
import by.bank.consumerlending.site.dto.resp.PagedListResponseDto;

import java.util.List;
import java.util.UUID;

public interface OperativeInfoService {

    PagedListResponseDto<OperativeInfoResponseDto> getAllOperativeInfo(int page, int pageSize);

    List<OperativeInfoShortResponseDto> getLatestOperativeInfo(int limit);

    OperativeInfoResponseDto getOperativeInfoById(UUID id);

    OperativeInfoResponseDto createOperativeInfo(OperativeInfoRequestDto dto, String username);

    OperativeInfoResponseDto updateOperativeInfo(UUID id, OperativeInfoRequestDto dto);

    void deleteOperativeInfo(UUID id);
}