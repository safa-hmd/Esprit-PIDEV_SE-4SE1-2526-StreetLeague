package com.example.streetleague.Controller;

import com.example.streetleague.domain.ChatMessage;
import com.example.streetleague.Repository.ChatMessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageRepository chatMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
    }

    // ── Envoi texte/image via WebSocket ──────────────────────────────────────
    @MessageMapping("/chat/{contratId}")
    public void processMessage(@DestinationVariable Long contratId, @Payload ChatMessage chatMessage) {



        chatMessage.setContratSponsorId(contratId);
        chatMessage.setTimestamp(new Date());

        try {
            ChatMessage savedMsg = chatMessageRepository.save(chatMessage);
            messagingTemplate.convertAndSend("/topic/chat/" + contratId, savedMsg);

        } catch (Exception e) {
            System.err.println("!!! Erreur Chat : " + e.getMessage());
        }
    }

    // ── Upload audio via REST (évite la limite de taille du WebSocket) ───────
    @PostMapping("/api/chat/send-audio")
    public ResponseEntity<Map<String, Object>> sendAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("contratId") Long contratId,
            @RequestParam("expediteur") String expediteur,
            @RequestParam("role") String role,
            @RequestParam(value = "duration", required = false, defaultValue = "0") String duration) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Convertir en Base64 et sauvegarder en DB
            String base64 = "data:" + file.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(file.getBytes());

            ChatMessage msg = new ChatMessage();
            msg.setContratSponsorId(contratId);
            msg.setExpediteur(expediteur);
            msg.setRole(role);
            msg.setContenu(duration); // La durée est stockée ici de manière fiable
            msg.setType("VOICE");
            msg.setMediaData(base64);
            msg.setTimestamp(new Date());

            ChatMessage saved = chatMessageRepository.save(msg);

            // Notifier via WebSocket avec le message COMPLET (depuis la DB, donc garanti sauvegardé)
            messagingTemplate.convertAndSend("/topic/chat/" + contratId, saved);

            response.put("success", true);
            response.put("id", saved.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("!!! Erreur upload audio : " + e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ── Historique ───────────────────────────────────────────────────────────
    @GetMapping("/api/chat/history/{contratId}")
    public List<ChatMessage> getChatHistory(@PathVariable Long contratId) {
        return chatMessageRepository.findByContratSponsorIdOrderByTimestampAsc(contratId);
    }

    // ── Suppression de message ───────────────────────────────────────────────
    @DeleteMapping("/api/chat/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id, @RequestParam("contratId") Long contratId) {
        try {
            chatMessageRepository.deleteById(id);
            // Notifier les clients
            ChatMessage deleteMsg = new ChatMessage();
            deleteMsg.setId(id);
            deleteMsg.setContratSponsorId(contratId);
            deleteMsg.setType("DELETE");
            messagingTemplate.convertAndSend("/topic/chat/" + contratId, deleteMsg);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // ── Ajouter une Réaction ─────────────────────────────────────────────────
    @PostMapping("/api/chat/{id}/react")
    public ResponseEntity<?> reactToMessage(@PathVariable Long id, @RequestParam("reaction") String reaction, @RequestParam("contratId") Long contratId) {
        try {
            ChatMessage msg = chatMessageRepository.findById(id).orElseThrow();
            
            // On encode la réaction pour éviter le crash MySQL (utf8 vs utf8mb4)
            String encodedReaction = "ENC:" + java.net.URLEncoder.encode(reaction, java.nio.charset.StandardCharsets.UTF_8.toString());
            msg.setReaction(encodedReaction);
            chatMessageRepository.save(msg);

            // Notifier les clients
            ChatMessage reactMsg = new ChatMessage();
            reactMsg.setId(id);
            reactMsg.setContratSponsorId(contratId);
            reactMsg.setType("REACTION");
            reactMsg.setReaction(encodedReaction);
            messagingTemplate.convertAndSend("/topic/chat/" + contratId, reactMsg);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}

