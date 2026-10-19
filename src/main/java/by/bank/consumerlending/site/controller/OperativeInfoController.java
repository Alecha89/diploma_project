package by.bank.consumerlending.site.controller;

import by.bank.consumerlending.site.dto.req.OperativeInfoRequestDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoResponseDto;
import by.bank.consumerlending.site.dto.resp.OperativeInfoShortResponseDto;
import by.bank.consumerlending.site.dto.resp.PagedListResponseDto;
import by.bank.consumerlending.site.service.OperativeInfoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/operative-info")
@RequiredArgsConstructor
@Validated
public class OperativeInfoController {

    private final OperativeInfoService operativeInfoService;

    @GetMapping()
    public ResponseEntity<PagedListResponseDto<OperativeInfoResponseDto>> getAll(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        return ResponseEntity.ok(operativeInfoService.getAllOperativeInfo(page, pageSize));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<OperativeInfoShortResponseDto>> getLatest(
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return ResponseEntity.ok(operativeInfoService.getLatestOperativeInfo(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperativeInfoResponseDto> getById(@PathVariable UUID id) {
        OperativeInfoResponseDto dto = operativeInfoService.getOperativeInfoById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<OperativeInfoResponseDto> create(@Valid @RequestBody OperativeInfoRequestDto dto,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operativeInfoService.createOperativeInfo(dto, username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OperativeInfoResponseDto> update(@PathVariable UUID id,
                                                           @Valid @RequestBody OperativeInfoRequestDto dto) {
        return ResponseEntity.ok(operativeInfoService.updateOperativeInfo(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        operativeInfoService.deleteOperativeInfo(id);
        return ResponseEntity.noContent().build();
    }
}