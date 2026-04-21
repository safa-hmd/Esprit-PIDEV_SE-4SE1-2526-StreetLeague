package com.example.streetleague.schedule;

import java.time.LocalDate;
import java.util.List;

public interface FieldScheduleService {

    List<FieldScheduleEntryDto> getFieldSchedule(Long fieldId, LocalDate from, LocalDate to);
}