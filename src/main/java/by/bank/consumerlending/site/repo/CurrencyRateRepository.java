package by.bank.consumerlending.site.repo;

import by.bank.consumerlending.site.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, String> {
}