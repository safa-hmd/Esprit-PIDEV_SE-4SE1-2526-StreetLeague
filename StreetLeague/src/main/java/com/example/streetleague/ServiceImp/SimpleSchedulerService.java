// SimpleSchedulerService.java
package com.example.streetleague.ServiceImp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class SimpleSchedulerService {

    //  S'exécute tous les jours à 07:00:00
    @Scheduled(cron = "0 0 7 * * *")
    public void simpleTask() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        log.info("⏰ Scheduler exécuté à: {}", LocalDateTime.now().format(formatter));
    }
}