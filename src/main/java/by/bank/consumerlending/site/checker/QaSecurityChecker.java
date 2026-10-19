package by.bank.consumerlending.site.checker;

import by.bank.consumerlending.site.dto.resp.QaQuestionResponseDto;
import by.bank.consumerlending.site.entity.QaStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("qaSecurityChecker")
public class QaSecurityChecker {

    @SuppressWarnings("unused")
    public boolean canViewQuestionsList(QaStatus status) {
        if (status == QaStatus.ANSWERED) {
            return true;
        }
        return isStaff();
    }

    @SuppressWarnings("unused")
    public boolean canViewQuestion(QaQuestionResponseDto question) {
        if (question == null) {
            return false;
        }

        if (question.status() == QaStatus.ANSWERED) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return isStaff();
    }

    public boolean isStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
    }
}