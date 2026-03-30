package com.example.streetleague.mapper;

import com.example.streetleague.domain.Communaute;
import com.example.streetleague.dto.CommunauteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunauteMapper {
    CommunauteDTO toDTO(Communaute entity);
    Communaute toEntity(CommunauteDTO dto);
}