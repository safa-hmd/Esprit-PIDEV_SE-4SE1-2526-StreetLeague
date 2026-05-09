package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.MaterielService;
import com.example.streetleague.dto.MaterielDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/materiels")
@RequiredArgsConstructor
public class MaterielController {

    private final MaterielService materielService;
    private static final String UPLOAD_DIR = "uploads/materiels/";

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

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Vérification type fichier
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Format non supporté.");
            }

            // Vérification taille (max 5 MB)
            if (file.getSize() > 20 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Fichier trop volumineux (max 5 MB).");
            }

            // Créer le dossier si inexistant
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom unique
            String extension = file.getOriginalFilename() != null
                    ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Retourner l'URL accessible
            String imageUrl = "http://localhost:8086/StreetLeague/uploads/materiels/" + filename;
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'upload.");
        }
    }

}