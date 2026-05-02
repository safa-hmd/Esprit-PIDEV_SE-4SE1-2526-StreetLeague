package com.example.streetleague.mapper;

import com.example.streetleague.Entity.SponsoringEvenement;
import com.example.streetleague.dto.SponsoringEvenementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SponsoringEvenementMapper {

    @Mapping(source = "sponsor.id", target = "sponsorId")
    @Mapping(source = "evenement.id", target = "evenementId")
    SponsoringEvenementDTO toDTO(SponsoringEvenement entity);

    @Mapping(source = "sponsorId", target = "sponsor.id")
    @Mapping(source = "evenementId", target = "evenement.id")
    SponsoringEvenement toEntity(SponsoringEvenementDTO dto);
}