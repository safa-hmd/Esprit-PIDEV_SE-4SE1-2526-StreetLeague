package com.example.streetleague.Controller;

import com.example.streetleague.Entity.waterReminder;
import com.example.streetleague.Repository.WaterReminderRepository;
import com.example.streetleague.ServiceInterface.waterReminderService;
import com.example.streetleague.dto.WaterReminderResponseDTO;
import com.example.streetleague.dto.waterReminderDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/waterReminder")
public class WaterReminderController {
    private final waterReminderService waterReminderService;
    private final WaterReminderRepository waterReminderRepository;


    @PostMapping("/add")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<waterReminder> addwaterReminder(@Valid @RequestBody waterReminderDTO dto) {
        return ResponseEntity.ok(waterReminderService.addwaterReminder(dto));
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<waterReminder> getwaterReminder(@PathVariable Long id) {
        return ResponseEntity.ok(waterReminderService.getwaterReminder(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<waterReminder> updatewaterReminder(@PathVariable Long id, @RequestBody waterReminderDTO dto) {
        return ResponseEntity.ok(waterReminderService.updatewaterReminder(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<Void> deletewaterReminder(@PathVariable Long id) {
        waterReminderService.deletewaterReminder(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WaterReminderResponseDTO>> getAllReminders() {
        return ResponseEntity.ok(waterReminderService.getAllReminders());
    }
    @GetMapping("/byUser/{userId}")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<waterReminder> getReminderByUser(@PathVariable Long userId) {
        return waterReminderRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
