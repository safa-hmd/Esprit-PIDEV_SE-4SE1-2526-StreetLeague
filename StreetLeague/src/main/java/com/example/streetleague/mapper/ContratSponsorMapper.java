package com.example.streetleague.mapper;

import com.example.streetleague.Entity.ContratSponsor;
import com.example.streetleague.dto.ContratSponsorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContratSponsorMapper {

    @Mapping(source = "sponsor.id",  target = "sponsorId")
    @Mapping(source = "sponsor.nom", target = "sponsorNom")
    @Mapping(source = "montant", target = "montantTotal")
    ContratSponsorDTO toDTO(ContratSponsor entity);

    @Mapping(source = "sponsorId", target = "sponsor.id")
    @Mapping(source = "montantTotal", target = "montant")
    ContratSponsor toEntity(ContratSponsorDTO dto);

    default LocalDate map(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    default Date map(LocalDate localDate) {
        if (localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }
}