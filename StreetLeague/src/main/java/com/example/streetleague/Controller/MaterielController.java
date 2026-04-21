package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.MaterielService;
import com.example.streetleague.dto.MaterielDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materiels")
@RequiredArgsConstructor
public class MaterielController {

    private final MaterielService materielService;

    @PostMapping
    public ResponseEntity<MaterielDTO> create(@Valid @RequestBody MaterielDTO dto) {
        return ResponseEntity.ok(materielService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterielDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody MaterielDTO dto) {
        return ResponseEntity.ok(materielService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterielDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(materielService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MaterielDTO>> getAll() {
        return ResponseEntity.ok(materielService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materielService.delete(id);
        return ResponseEntity.noContent().build();
    }
}