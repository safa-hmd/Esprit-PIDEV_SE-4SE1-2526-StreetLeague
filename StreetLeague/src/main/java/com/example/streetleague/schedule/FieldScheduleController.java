package com.example.streetleague.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FieldScheduleController {

    private final FieldScheduleService fieldScheduleService;

    /**
     * GET /api/fields/{id}/schedule?from=2026-04-01&to=2026-04-30
     */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<FieldScheduleEntryDto>> getFieldSchedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(fieldScheduleService.getFieldSchedule(id, from, to));
    }
}