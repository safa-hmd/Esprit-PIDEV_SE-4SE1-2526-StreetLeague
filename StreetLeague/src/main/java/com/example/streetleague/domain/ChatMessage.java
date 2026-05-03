package com.example.streetleague.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contratSponsorId;
    private String expediteur;

    @Column(columnDefinition = "LONGTEXT")
    private String contenu; // Pour le texte ou le base64 court

    private String type; // TEXT, IMAGE, VOICE

    @Column(columnDefinition = "LONGTEXT")
    private String mediaData; // Pour stocker les images/vocaux en base64 (plus simple pour ce projet)

    private String role; // Rôle de l'expéditeur (SPONSOR, ADMIN, etc.)

    private String reaction; // Emojis like ❤️, 👍, etc.

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContratSponsorId() { return contratSponsorId; }
    public void setContratSponsorId(Long contratSponsorId) { this.contratSponsorId = contratSponsorId; }
    public String getExpediteur() { return expediteur; }
    public void setExpediteur(String expediteur) { this.expediteur = expediteur; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMediaData() { return mediaData; }
    public void setMediaData(String mediaData) { this.mediaData = mediaData; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
