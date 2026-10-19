package by.bank.consumerlending.site.entity.specification;

import by.bank.consumerlending.site.entity.QaQuestion;
import by.bank.consumerlending.site.entity.QaStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class QaQuestionSpecification {

    private QaQuestionSpecification() {
    }

    public static Specification<QaQuestion> specificationIsNotDeleted() {
        return (root, query, criteriaBuilder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("topic", JoinType.INNER);
            }
            return criteriaBuilder.equal(root.get("topic").get("deleted"), false);
        };
    }

    public static Specification<QaQuestion> specificationByStatus(QaStatus status) {
        return (root, _, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<QaQuestion> specificationByTopicId(UUID topicId) {
        return (root, _, cb) ->
                topicId == null ? cb.conjunction() : cb.equal(root.get("topic").get("id"), topicId);
    }
}