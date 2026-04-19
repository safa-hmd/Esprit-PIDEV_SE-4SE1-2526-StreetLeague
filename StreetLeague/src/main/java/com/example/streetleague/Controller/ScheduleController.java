package com.example.streetleague.Controller;

import com.example.streetleague.ServiceImp.ScheduleService;
import com.example.streetleague.dto.ScheduleEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{userId}/week")
    public ResponseEntity<List<ScheduleEventDto>> week(@PathVariable Long userId) {
        LocalDateTime from = LocalDateTime.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
        return ResponseEntity.ok(scheduleService.getSchedule(userId, from, from.plusDays(7)));
    }

    @GetMapping("/{userId}/month")
    public ResponseEntity<List<ScheduleEventDto>> month(@PathVariable Long userId) {
        LocalDateTime from = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime to   = LocalDateTime.now()
                .with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59);
        return ResponseEntity.ok(scheduleService.getSchedule(userId, from, to));
    }
}
