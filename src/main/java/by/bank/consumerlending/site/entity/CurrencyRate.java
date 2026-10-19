package by.bank.consumerlending.site.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "currency_rate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CurrencyRate {

    @Id
    private String abbreviation;

    @Column
    private Integer scale;

    @Column
    private BigDecimal rate;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyRate currencyRate)) return false;
        return abbreviation != null && abbreviation.equals(currencyRate.getAbbreviation());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}