package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.dto.FieldDto;

import java.util.List;

public interface IFieldService {
    FieldDto createField(FieldDto dto);
    FieldDto getFieldById(Long id);
    List<FieldDto> getAllFields();
    List<FieldDto> getAvailableFields();
    List<FieldDto> getFieldsBySport(SportType sportType);
    FieldDto updateField(Long id, FieldDto dto);
    void deleteField(Long id);
    FieldDto toggleAvailability(Long id);


    FieldDto geocodeField(Long id);
    List<FieldDto> getAllFieldsWithGps();
}
