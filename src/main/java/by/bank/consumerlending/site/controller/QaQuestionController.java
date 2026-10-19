package by.bank.consumerlending.site.controller;

import by.bank.consumerlending.site.checker.QaSecurityChecker;
import by.bank.consumerlending.site.dto.req.QaAnswerRequestDto;
import by.bank.consumerlending.site.dto.req.QaQuestionRequestDto;
import by.bank.consumerlending.site.dto.resp.PagedListResponseDto;
import by.bank.consumerlending.site.dto.resp.QaQuestionResponseDto;
import by.bank.consumerlending.site.entity.QaStatus;
import by.bank.consumerlending.site.service.QaQuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/qa")
@RequiredArgsConstructor
@Validated
@Slf4j
public class QaQuestionController {

    private final QaQuestionService questionService;

    private final QaSecurityChecker securityChecker;

    @GetMapping("/questions")
    public ResponseEntity<PagedListResponseDto<QaQuestionResponseDto>> getQuestions(
            @RequestParam(required = false) QaStatus status,
            @RequestParam(required = false) UUID topicId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int pageSize) {

        if (status == QaStatus.PENDING && !securityChecker.isStaff()) {
            throw new AuthorizationDeniedException("Доступ к неотвеченным вопросам запрещен");
        }
        QaStatus finalStatus = !securityChecker.isStaff() ? QaStatus.ANSWERED : status;

        return ResponseEntity.ok(questionService.getQuestions(finalStatus, topicId, page, pageSize));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/questions/mine")
    public ResponseEntity<List<QaQuestionResponseDto>> getMyQuestions(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(questionService.getMyQuestions(username));
    }

    @PostAuthorize("@qaSecurityChecker.canViewQuestion(returnObject.body)")
    @GetMapping("/questions/{id}")
    public ResponseEntity<QaQuestionResponseDto> getQuestionById(@PathVariable UUID id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/questions")
    public ResponseEntity<QaQuestionResponseDto> createQuestion(@RequestBody @Valid QaQuestionRequestDto dto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(dto, username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/questions/{id}/answer")
    public ResponseEntity<QaQuestionResponseDto> answerQuestion(@PathVariable UUID id,
                                                                @RequestBody @Valid QaAnswerRequestDto dto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(questionService.answerQuestion(id, dto, username));
    }
}