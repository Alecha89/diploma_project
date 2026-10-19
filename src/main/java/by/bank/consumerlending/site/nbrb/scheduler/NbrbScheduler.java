package by.bank.consumerlending.site.nbrb.scheduler;

import by.bank.consumerlending.site.nbrb.client.NbrbClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class NbrbScheduler {

    private final NbrbClient nbrbClient;

    @Scheduled(cron = "${site.nbrb.cron}")
    public void scheduledStaffSync() {
        log.info("Запуск плановой синхронизации курсов валют...");
        nbrbClient.executeSync();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Profile("!test")
    public void onApplicationReady() {
        log.info("Приложение запущено. Выполняется синхронизации курсов валют с API Национального банка РБ...");
        nbrbClient.executeSync();
    }
}