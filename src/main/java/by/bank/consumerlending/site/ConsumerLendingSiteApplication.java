package by.bank.consumerlending.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class ConsumerLendingSiteApplication {

    static void main(String[] args) {
        SpringApplication.run(ConsumerLendingSiteApplication.class, args);
    }
}
