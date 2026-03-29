package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.ServiceInterface.IFieldService;
import com.example.streetleague.Entity.Field;
import com.example.streetleague.Entity.SportType;
import com.example.streetleague.dto.FieldDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FieldServiceImp implements IFieldService {

    private final FieldRepository fieldRepository;

    @Override
    public FieldDto createField(FieldDto dto) {
        Field field = Field.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sportType(dto.getSportType())
                .location(dto.getLocation())
                .imageUrl(dto.getImageUrl())
                .pricePerHour(dto.getPricePerHour())
                .capacity(dto.getCapacity())
                .available(true)
                .build();
        return mapToDto(fieldRepository.save(field));
    }

    @Override
    @Transactional(readOnly = true)
    public FieldDto getFieldById(Long id) {
        return mapToDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldDto> getAllFields() {
        return fieldRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldDto> getAvailableFields() {
        return fieldRepository.findByAvailable(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldDto> getFieldsBySport(SportType sportType) {
        return fieldRepository.findBySportType(sportType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FieldDto updateField(Long id, FieldDto dto) {
        Field field = findById(id);
        field.setName(dto.getName());
        field.setDescription(dto.getDescription());
        field.setSportType(dto.getSportType());
        field.setLocation(dto.getLocation());
        field.setImageUrl(dto.getImageUrl());
        field.setPricePerHour(dto.getPricePerHour());
        field.setCapacity(dto.getCapacity());
        field.setAvailable(dto.isAvailable());
        return mapToDto(fieldRepository.save(field));
    }

    @Override
    public void deleteField(Long id) {
        Field field = findById(id);
        fieldRepository.delete(field);
    }

    @Override
    public FieldDto toggleAvailability(Long id) {
        Field field = findById(id);
        field.setAvailable(!field.isAvailable());
        return mapToDto(fieldRepository.save(field));
    }

    // ---- Helpers ----

    private Field findById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));
    }

    private FieldDto mapToDto(Field f) {
        return FieldDto.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .sportType(f.getSportType())
                .location(f.getLocation())
                .imageUrl(f.getImageUrl())
                .pricePerHour(f.getPricePerHour())
                .capacity(f.getCapacity())
                .available(f.isAvailable())
                .build();
    }
}
