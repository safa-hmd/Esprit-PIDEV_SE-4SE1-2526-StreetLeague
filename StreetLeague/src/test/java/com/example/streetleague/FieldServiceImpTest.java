package com.example.streetleague;

import com.example.streetleague.Entity.Field;
import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.ServiceImp.FieldServiceImp;
import com.example.streetleague.dto.FieldDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldServiceImpTest {

    @Mock  FieldRepository fieldRepository;
    @InjectMocks FieldServiceImp service;

    private FieldDto validDto;
    private Field savedField;

    @BeforeEach
    void setUp() {
        validDto = FieldDto.builder()
                .name("Central Court")
                .sportType(SportType.FOOTBALL)
                .location("Tunis")
                .pricePerHour(80.0)
                .capacity(11)
                .available(true)
                .build();

        savedField = Field.builder()
                .id(1L)
                .name("Central Court")
                .sportType(SportType.FOOTBALL)
                .location("Tunis")
                .pricePerHour(80.0)
                .capacity(11)
                .available(true)
                .build();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    void createField_success() {
        when(fieldRepository.save(any())).thenReturn(savedField);

        FieldDto result = service.createField(validDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Central Court");
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getPricePerHour()).isEqualTo(80.0);
        verify(fieldRepository).save(any(Field.class));
    }

    @Test
    void createField_alwaysAvailableOnCreation() {
        validDto.setAvailable(false); // même si on passe false
        when(fieldRepository.save(any())).thenReturn(savedField);

        FieldDto result = service.createField(validDto);

        // Le service force available=true à la création
        assertThat(result.isAvailable()).isTrue();
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Test
    void getFieldById_success() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(savedField));

        FieldDto result = service.getFieldById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Central Court");
    }

    @Test
    void getFieldById_notFound_throwsException() {
        when(fieldRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFieldById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Field not found");
    }

    @Test
    void getAllFields_returnsList() {
        when(fieldRepository.findAll()).thenReturn(List.of(savedField));

        List<FieldDto> result = service.getAllFields();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Central Court");
    }

    @Test
    void getAvailableFields_returnsOnlyAvailable() {
        when(fieldRepository.findByAvailable(true)).thenReturn(List.of(savedField));

        List<FieldDto> result = service.getAvailableFields();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isTrue();
    }

    @Test
    void getFieldsBySport_returnsFiltered() {
        when(fieldRepository.findBySportType(SportType.FOOTBALL))
                .thenReturn(List.of(savedField));

        List<FieldDto> result = service.getFieldsBySport(SportType.FOOTBALL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSportType()).isEqualTo(SportType.FOOTBALL);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    void updateField_success() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(savedField));
        when(fieldRepository.save(any())).thenReturn(savedField);

        validDto.setName("Updated Court");
        validDto.setPricePerHour(100.0);

        FieldDto result = service.updateField(1L, validDto);

        assertThat(result).isNotNull();
        verify(fieldRepository).save(any(Field.class));
    }

    @Test
    void updateField_notFound_throwsException() {
        when(fieldRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateField(99L, validDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Field not found");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void deleteField_success() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(savedField));

        service.deleteField(1L);

        verify(fieldRepository).delete(savedField);
    }

    @Test
    void deleteField_notFound_throwsException() {
        when(fieldRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteField(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── TOGGLE AVAILABILITY ───────────────────────────────────────────────────

    @Test
    void toggleAvailability_fromTrueToFalse() {
        savedField.setAvailable(true);
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(savedField));
        when(fieldRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FieldDto result = service.toggleAvailability(1L);

        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void toggleAvailability_fromFalseToTrue() {
        savedField.setAvailable(false);
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(savedField));
        when(fieldRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FieldDto result = service.toggleAvailability(1L);

        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    void toggleAvailability_notFound_throwsException() {
        when(fieldRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggleAvailability(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}