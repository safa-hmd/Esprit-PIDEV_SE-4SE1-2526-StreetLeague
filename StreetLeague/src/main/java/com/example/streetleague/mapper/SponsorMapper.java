package com.example.streetleague.mapper;

import com.example.streetleague.domain.Sponsor;
import com.example.streetleague.dto.SponsorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SponsorMapper {
    SponsorDTO toDTO(Sponsor entity);
    Sponsor toEntity(SponsorDTO dto);
}