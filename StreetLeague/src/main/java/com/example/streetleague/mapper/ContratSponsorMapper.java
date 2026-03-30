package com.example.streetleague.mapper;

import com.example.streetleague.domain.ContratSponsor;
import com.example.streetleague.dto.ContratSponsorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContratSponsorMapper {

    @Mapping(source = "sponsor.id", target = "sponsorId")
    ContratSponsorDTO toDTO(ContratSponsor entity);

    @Mapping(source = "sponsorId", target = "sponsor.id")
    ContratSponsor toEntity(ContratSponsorDTO dto);
}