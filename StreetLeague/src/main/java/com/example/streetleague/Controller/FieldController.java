package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.IFieldService;
import com.example.streetleague.Entity.SportType;
import com.example.streetleague.dto.FieldDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FieldController {

    private final IFieldService fieldService;

    // ===== ADMIN =====

    @PostMapping
    public ResponseEntity<FieldDto> create(@RequestBody FieldDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fieldService.createField(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldDto> update(@PathVariable Long id,
                                           @RequestBody FieldDto dto) {
        return ResponseEntity.ok(fieldService.updateField(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fieldService.deleteField(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<FieldDto> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.toggleAvailability(id));
    }

    // ===== ADMIN + PLAYER =====

    @GetMapping
    public ResponseEntity<List<FieldDto>> getAll() {
        return ResponseEntity.ok(fieldService.getAllFields());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.getFieldById(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<FieldDto>> getAvailable() {
        return ResponseEntity.ok(fieldService.getAvailableFields());
    }

    @GetMapping("/sport/{sportType}")
    public ResponseEntity<List<FieldDto>> getBySport(@PathVariable SportType sportType) {
        return ResponseEntity.ok(fieldService.getFieldsBySport(sportType));
    }
}