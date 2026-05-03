package com.example.streetleague.Scheduler;

import com.example.streetleague.ServiceImp.EmailService;
import com.example.streetleague.ServiceInterface.EvenementCommunauteService;
import com.example.streetleague.dto.EvenementSansSponsoringDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventAlertScheduler {

    private final EvenementCommunauteService evenementCommunauteService;
    private final EmailService emailService;

    // Le système envoie le mail via le SMTP de Safa, mais l'alerte est reçue par Iyed !
    private final String ADMIN_EMAIL = "iyedessid11@gmail.com"; 

    public EventAlertScheduler(EvenementCommunauteService evenementCommunauteService, EmailService emailService) {
        this.evenementCommunauteService = evenementCommunauteService;
        this.emailService = emailService;
    }

    // S'exécute toutes les minutes pour les tests.
    // Pour une vraie prod : "0 0 8 * * ?" (Tous les jours à 8h00)
    @Scheduled(fixedRate = 60000) 
    public void checkForOrphanEvents() {
        System.out.println("⏳ [Scheduler] Vérification des événements sans sponsoring...");
        
        List<EvenementSansSponsoringDTO> orphelins = evenementCommunauteService.getEvenementsSansSponsoring();
        
        if (!orphelins.isEmpty()) {
            System.out.println("⚠️ " + orphelins.size() + " événements orphelins trouvés. Envoi de l'alerte Email !");
            sendAlertEmail(orphelins);
        } else {
            System.out.println("✅ Aucun événement orphelin. Tout est sponsorisé !");
        }
    }

    private void sendAlertEmail(List<EvenementSansSponsoringDTO> orphelins) {
        String subject = "⚠️ ALERTE : Événements sans Sponsors !";
        
        StringBuilder htmlBody = new StringBuilder();
        htmlBody.append("<h2>Bonjour Admin,</h2>");
        htmlBody.append("<p>Le système automatisé StreetLeague a détecté <b>")
                .append(orphelins.size())
                .append("</b> événement(s) qui n'ont actuellement <strong>aucun soutien financier</strong> (Sponsoring).</p>");
        
        htmlBody.append("<table border='1' cellpadding='10' style='border-collapse: collapse; width: 100%;'>");
        htmlBody.append("<tr style='background-color: #f2f2f2;'><th>Communauté</th><th>Événement</th><th>Date</th></tr>");
        
        for (EvenementSansSponsoringDTO dto : orphelins) {
            htmlBody.append("<tr>");
            htmlBody.append("<td>").append(dto.getCommunauteNom()).append("</td>");
            htmlBody.append("<td>").append(dto.getEvenementTitre()).append("</td>");
            htmlBody.append("<td>").append(dto.getEvenementDate()).append("</td>");
            htmlBody.append("</tr>");
        }
        
        htmlBody.append("</table>");
        htmlBody.append("<br><p>Merci d'intervenir rapidement pour trouver des sponsors à ces événements.</p>");
        htmlBody.append("<p><i>Ceci est un email automatique généré par le Scheduler de StreetLeague.</i></p>");
        
        emailService.sendHtmlEmail(ADMIN_EMAIL, subject, htmlBody.toString());
    }
}
