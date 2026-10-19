package by.bank.consumerlending.site.controller;

import by.bank.consumerlending.site.dto.req.QaTopicCreateRequestDto;
import by.bank.consumerlending.site.dto.req.QaTopicUpdateRequestDto;
import by.bank.consumerlending.site.dto.resp.QaTopicResponseDto;
import by.bank.consumerlending.site.service.QaTopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/qa")
@RequiredArgsConstructor
@Validated
@Slf4j
public class QaTopicController {

    private final QaTopicService topicService;

    @GetMapping("/topics")
    public ResponseEntity<List<QaTopicResponseDto>> getActiveTopics() {
        return ResponseEntity.ok(topicService.getActiveTopics());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/topics/all")
    public ResponseEntity<List<QaTopicResponseDto>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/topics")
    public ResponseEntity<QaTopicResponseDto> createTopic(@RequestBody @Valid QaTopicCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.createTopic(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/topics/{id}")
    public ResponseEntity<QaTopicResponseDto> updateTopic(@PathVariable UUID id,
                                                          @RequestBody @Valid QaTopicUpdateRequestDto dto) {
        return ResponseEntity.ok(topicService.updateTopic(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        topicService.deleteTopic(id, username);
        return ResponseEntity.noContent().build();
    }
}