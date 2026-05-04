package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.PanierRepository;
import com.example.streetleague.domain.Panier;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoSchedulerService {

    private final PanierRepository panierRepository;
    private final PromoEngineService promoEngineService;

    @Scheduled(fixedRate = 3600000) // 1h
    public void checkAbandonedCarts() {

        List<Panier> paniers = panierRepository.findAll();

        for (Panier p : paniers) {

            if (p.getUpdatedAt() != null &&
                    p.getUpdatedAt().isBefore(LocalDateTime.now().minusHours(48))) {

                promoEngineService.generateAbandonedCartPromo(p.getUser());
            }
        }
    }
}