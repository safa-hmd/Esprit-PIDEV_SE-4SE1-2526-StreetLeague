package com.example.streetleague;

import com.example.streetleague.mapper.CommunauteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunauteServiceImpTest {

    @Mock
    private CommunauteRepository repo;
    @Mock
    private CommunauteMapper mapper;
    @InjectMocks
    private CommunauteServiceImp service;

    private CommunauteDTO dto;
    private Communaute entity;

    @BeforeEach
    void setUp() {
        dto = new CommunauteDTO(1L, "Street", "Desc", "Sport", new Date(), 10L);
        entity = new Communaute();
        entity.setId(1L);
    }

    @Test
    void create_shouldReturnSavedDto() {
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        CommunauteDTO result = service.create(dto);

        assertEquals(dto, result);
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getById(99L));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(5L);

        verify(repo).deleteById(5L);
    }
}
