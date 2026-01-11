package com.org.emprunt.controller;

import com.org.emprunt.DTO.EmpruntDetailsDTO;
import com.org.emprunt.entities.Emprunter;
import com.org.emprunt.service.EmpruntService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprunts")
public class EmpruntController {

    private final EmpruntService service;

    public EmpruntController(EmpruntService service) {
        this.service = service;
    }

    @PostMapping("/{userId}/{bookId}")
    public Emprunter createEmprunt(@PathVariable Long userId, @PathVariable Long bookId) {
        return service.createEmprunt(userId, bookId);
    }

    @GetMapping
    public List<Emprunter> getAllEmprunts() {
        return service.getAllEmprunts();
    }

    @GetMapping("/{id}")
    public Emprunter getEmpruntById(@PathVariable Long id) {
        return service.getEmpruntById(id);
    }

    @GetMapping("/{id}/details")
    public EmpruntDetailsDTO getEmpruntDetails(@PathVariable Long id) {
        return service.getEmpruntDetails(id);
    }
}