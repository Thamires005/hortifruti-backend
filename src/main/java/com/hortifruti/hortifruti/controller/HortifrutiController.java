package com.hortifruti.hortifruti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.HttpStatus;

import com.hortifruti.hortifruti.model.Hortifruti;
import com.hortifruti.hortifruti.service.HortifrutiService;
import com.hortifruti.hortifruti.*;




@restController
@RequestMapping("/api/hortifruti")

public class hortifrutiController {

    @Autowired
    private hortifrutiService hortifrutiService;

    @GetMapping
    public ResponseEntity<List<Hortifruti>> getAllHortifruti() {
        List<Hortifruti> hortifrutiList = hortifrutiService.getAllHortifruti();
        return ResponseEntity.ok(hortifrutiList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hortifruti> getHortifrutiById(@PathVariable Long id) {
        Hortifruti hortifruti = hortifrutiService.getHortifrutiById(id);
        return ResponseEntity.ok(hortifruti);
    }

    @PostMapping
    public ResponseEntity<Hortifruti> createHortifruti(@RequestBody Hortifruti hortifruti) {
        Hortifruti createdHortifruti = hortifrutiService.createHortifruti(hortifruti);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHortifruti);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hortifruti> updateHortifruti(@PathVariable Long id, @RequestBody Hortifruti hortifruti) {
        Hortifruti updatedHortifruti = hortifrutiService.updateHortifruti(id, hortifruti);
        return ResponseEntity.ok(updatedHortifruti);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHortifruti(@PathVariable Long id) {
        hortifrutiService.deleteHortifruti(id);
        return ResponseEntity.noContent().build();
    }
}



