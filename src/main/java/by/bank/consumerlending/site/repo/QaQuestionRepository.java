package by.bank.consumerlending.site.repo;

import by.bank.consumerlending.site.entity.QaQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QaQuestionRepository extends JpaRepository<QaQuestion, UUID> {

    @EntityGraph(attributePaths = {"topic"})
    Optional<QaQuestion> findWithTopicByIdAndTopicDeletedFalse(UUID id);

    long countByAuthorNameAndTopicIsActiveTrueAndTopicDeletedFalse(String authorId);

    List<QaQuestion> findAllByAuthorNameAndTopicIsActiveTrueAndTopicDeletedFalseOrderByCreatedAtDesc(String authorId);

    @EntityGraph(attributePaths = {"topic"})
    Page<QaQuestion> findAll(Specification<QaQuestion> spec, Pageable pageable);
}