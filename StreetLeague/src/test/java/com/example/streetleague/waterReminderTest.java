package com.example.streetleague;

import com.example.streetleague.Controller.WaterReminderController;
import com.example.streetleague.Entity.waterReminder;
import com.example.streetleague.ServiceInterface.waterReminderService;
import com.example.streetleague.dto.WaterReminderResponseDTO;
import com.example.streetleague.dto.waterReminderDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaterReminderControllerTest {

    @Mock
    private waterReminderService waterReminderService;

    @InjectMocks
    private WaterReminderController waterReminderController;

    private waterReminder createReminder() {
        waterReminder reminder = new waterReminder();
        reminder.setId(1L);
        return reminder;
    }

    private waterReminderDTO createDTO() {
        waterReminderDTO dto = new waterReminderDTO();
        return dto;
    }

    @Test
    void shouldAddWaterReminder() {
        waterReminder reminder = createReminder();
        waterReminderDTO dto = createDTO();

        when(waterReminderService.addwaterReminder(dto)).thenReturn(reminder);

        ResponseEntity<waterReminder> response = waterReminderController.addwaterReminder(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(reminder, response.getBody());

        verify(waterReminderService).addwaterReminder(dto);
    }

    @Test
    void shouldGetWaterReminderById() {
        waterReminder reminder = createReminder();

        when(waterReminderService.getwaterReminder(1L)).thenReturn(reminder);

        ResponseEntity<waterReminder> response = waterReminderController.getwaterReminder(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(reminder, response.getBody());

        verify(waterReminderService).getwaterReminder(1L);
    }

    @Test
    void shouldUpdateWaterReminder() {
        waterReminder reminder = createReminder();
        waterReminderDTO dto = createDTO();

        when(waterReminderService.updatewaterReminder(1L, dto)).thenReturn(reminder);

        ResponseEntity<waterReminder> response =
                waterReminderController.updatewaterReminder(1L, dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(reminder, response.getBody());

        verify(waterReminderService).updatewaterReminder(1L, dto);
    }

    @Test
    void shouldDeleteWaterReminder() {
        doNothing().when(waterReminderService).deletewaterReminder(1L);

        ResponseEntity<Void> response = waterReminderController.deletewaterReminder(1L);

        assertEquals(204, response.getStatusCode().value());

        verify(waterReminderService).deletewaterReminder(1L);
    }

    @Test
    void shouldGetAllReminders() {
        WaterReminderResponseDTO dto = new WaterReminderResponseDTO();

        when(waterReminderService.getAllReminders()).thenReturn(List.of(dto));

        ResponseEntity<List<WaterReminderResponseDTO>> response =
                waterReminderController.getAllReminders();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(waterReminderService).getAllReminders();
    }
}