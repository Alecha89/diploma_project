package by.bank.consumerlending.site.repo;

import by.bank.consumerlending.site.entity.OperativeInfo;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperativeInfoRepository extends JpaRepository<OperativeInfo, UUID> {

    List<OperativeInfo> findBy(Limit limit, Sort sort);
}