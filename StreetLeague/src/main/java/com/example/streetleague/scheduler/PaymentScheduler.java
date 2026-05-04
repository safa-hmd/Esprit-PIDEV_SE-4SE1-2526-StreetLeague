package com.example.streetleague.scheduler;

import com.example.streetleague.Entity.Payment;
import com.example.streetleague.Entity.PaymentStatus;
import com.example.streetleague.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private static final Logger log = LoggerFactory.getLogger(PaymentScheduler.class);

    private final PaymentRepository paymentRepository;

    /**
     * Tourne chaque nuit à minuit.
     * Expire les paiements PENDING depuis plus de 24h → FAILED.
     */
    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(fixedRate = 30000)
    @Transactional
    public void expireUnpaidPayments() {

        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        // LocalDateTime cutoff = LocalDateTime.now().minusMinutes(1);

        //List<Payment> expired = paymentRepository.findPendingOlderThan(cutoff);
        List<Payment> expired = paymentRepository.findPendingOlderThan(
                PaymentStatus.PENDING,
                cutoff
        );

        if (expired.isEmpty()) {
//            log.info("[PaymentScheduler] No expired payments found.");
            return;
        }

        for (Payment p : expired) {
            p.setStatus(PaymentStatus.FAILED);
//            log.info("[PaymentScheduler] Payment #{} expired — reservation #{}",
//                    p.getId(), p.getReservation().getId());
        }

        paymentRepository.saveAll(expired);
//        log.info("[PaymentScheduler] {} payment(s) marked as FAILED.", expired.size());
    }
}