package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.IFieldService;
import com.example.streetleague.Entity.SportType;
import com.example.streetleague.dto.FieldDto;
import jakarta.validation.Valid;
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
    public ResponseEntity<FieldDto> create(@Valid @RequestBody FieldDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fieldService.createField(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldDto> update(@Valid @PathVariable Long id,
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




    /// //
    // Géocoder un terrain par son id
    @PostMapping("/{id}/geocode")
    public ResponseEntity<FieldDto> geocode(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.geocodeField(id));
    }

    // Géocoder TOUS les terrains sans GPS d'un seul appel
    @PostMapping("/geocode-all")
    public ResponseEntity<String> geocodeAll() throws InterruptedException {
        List<FieldDto> all = fieldService.getAllFields();
        int count = 0;
        for (FieldDto f : all) {
            if (f.getLatitude() == null) {
                try {
                    fieldService.geocodeField(f.getId());
                    count++;
                    Thread.sleep(1100); // rate limit Nominatim = 1 req/sec
                } catch (Exception e) {
                    System.out.println("⚠️ Échec: " + f.getName() + " → " + e.getMessage());
                }
            }
        }
        return ResponseEntity.ok("✅ Géocodé " + count + " terrains !");
    }

    @PostMapping("/geocode-all-force")
    public ResponseEntity<String> geocodeAllForce() throws InterruptedException {
        List<FieldDto> all = fieldService.getAllFields();
        int count = 0;
        for (FieldDto f : all) {
            try {
                fieldService.geocodeField(f.getId()); // force même si lat existe
                count++;
                Thread.sleep(1100);
            } catch (Exception e) {
                System.out.println("⚠️ Échec: " + f.getName() + " → " + e.getMessage());
            }
        }
        return ResponseEntity.ok("✅ Regéocodé " + count + " terrains !");
    }

    // Retourne uniquement les terrains avec GPS (pour la map)
    @GetMapping("/with-gps")
    public ResponseEntity<List<FieldDto>> getWithGps() {
        return ResponseEntity.ok(fieldService.getAllFieldsWithGps());
    }

}