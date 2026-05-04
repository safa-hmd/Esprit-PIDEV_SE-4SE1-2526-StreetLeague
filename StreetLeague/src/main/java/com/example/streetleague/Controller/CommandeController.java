package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.CommandeService;
import com.example.streetleague.domain.Commande;
import com.example.streetleague.dto.CommandeDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin("*")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    public ResponseEntity<Commande> create(@Valid @RequestBody CommandeDTO dto) {
        return ResponseEntity.ok(commandeService.createCommande(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Commande> updateStatus(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(commandeService.updateCommandeStatus(id, statut));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Commande> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getCommandeById(id));
    }

    @GetMapping
    public ResponseEntity<List<Commande>> getAll() {
        return ResponseEntity.ok(commandeService.getAllCommandes());
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<String> checkout(@PathVariable Long userId) {
        Long commandeId = commandeService.checkout(userId);
        return ResponseEntity.ok("Commande créée avec succès. ID = " + commandeId);
    }
}