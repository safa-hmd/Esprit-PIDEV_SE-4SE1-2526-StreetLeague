package com.example.streetleague.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class CommandeDTO {

    @NotNull(message = "User est obligatoire")
    private Long userId;

    @NotNull(message = "Statut est obligatoire")
    private String statut;

    @NotNull(message = "Lignes de commande sont obligatoires")
    private List<LigneCommandeDTO> lignes;

    public CommandeDTO() {
    }

    public CommandeDTO(Long userId, String statut, List<LigneCommandeDTO> lignes) {
        this.userId = userId;
        this.statut = statut;
        this.lignes = lignes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<LigneCommandeDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommandeDTO> lignes) {
        this.lignes = lignes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandeDTO that = (CommandeDTO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(statut, that.statut) && Objects.equals(lignes, that.lignes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, statut, lignes);
    }

    @Override
    public String toString() {
        return "CommandeDTO{" +
                "userId=" + userId +
                ", statut='" + statut + '\'' +
                ", lignes=" + lignes +
                '}';
    }

    public static CommandeDTOBuilder builder() {
        return new CommandeDTOBuilder();
    }

    public static class CommandeDTOBuilder {
        private Long userId;
        private String statut;
        private List<LigneCommandeDTO> lignes;

        public CommandeDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public CommandeDTOBuilder statut(String statut) { this.statut = statut; return this; }
        public CommandeDTOBuilder lignes(List<LigneCommandeDTO> lignes) { this.lignes = lignes; return this; }

        public CommandeDTO build() {
            return new CommandeDTO(userId, statut, lignes);
        }
    }
}