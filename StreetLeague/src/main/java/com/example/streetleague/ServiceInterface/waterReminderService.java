package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.waterReminder;
import com.example.streetleague.dto.WaterReminderResponseDTO;
import com.example.streetleague.dto.waterReminderDTO;

import java.util.List;

public interface waterReminderService {
    waterReminder getwaterReminder(Long id);
    waterReminder updatewaterReminder(Long id, waterReminderDTO dto);
    waterReminder addwaterReminder(waterReminderDTO dto);
    void deletewaterReminder(Long id);
    List<WaterReminderResponseDTO> getAllReminders();}
