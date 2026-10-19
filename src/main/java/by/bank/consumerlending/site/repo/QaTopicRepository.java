package by.bank.consumerlending.site.repo;

import by.bank.consumerlending.site.entity.QaTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QaTopicRepository extends JpaRepository<QaTopic, UUID> {

    List<QaTopic> findAllByIsActiveTrueAndDeletedFalseOrderBySortOrderAscLabelAsc();

    List<QaTopic> findAllByDeletedFalseOrderBySortOrderAscLabelAsc();
}