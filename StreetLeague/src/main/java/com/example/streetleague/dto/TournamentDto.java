package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDto {
    private Long id;
    private String name;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;

    // ===== EXPLICIT GETTERS/SETTERS =====
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return this.city; }
    public void setCity(String city) { this.city = city; }

    public LocalDate getStartDate() { return this.startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return this.endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    // ===== STATIC BUILDER HELPER =====
    public static TournamentDtoBuilder builder() {
        return new TournamentDtoBuilder();
    }

    public static class TournamentDtoBuilder {
        private Long id;
        private String name;
        private String city;
        private LocalDate startDate;
        private LocalDate endDate;

        public TournamentDtoBuilder id(Long id) { this.id = id; return this; }
        public TournamentDtoBuilder name(String name) { this.name = name; return this; }
        public TournamentDtoBuilder city(String city) { this.city = city; return this; }
        public TournamentDtoBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public TournamentDtoBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }

        public TournamentDto build() {
            TournamentDto dto = new TournamentDto();
            dto.setId(id);
            dto.setName(name);
            dto.setCity(city);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            return dto;
        }
    }
}
