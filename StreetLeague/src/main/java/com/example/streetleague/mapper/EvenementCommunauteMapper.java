package com.example.streetleague.mapper;

import com.example.streetleague.domain.EvenementCommunaute;
import com.example.streetleague.dto.EvenementCommunauteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EvenementCommunauteMapper {

    @Mapping(source = "communaute.id", target = "communauteId")
    EvenementCommunauteDTO toDTO(EvenementCommunaute entity);

    @Mapping(source = "communauteId", target = "communaute.id")
    EvenementCommunaute toEntity(EvenementCommunauteDTO dto);
}