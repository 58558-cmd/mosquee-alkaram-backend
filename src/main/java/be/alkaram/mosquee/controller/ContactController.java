package be.alkaram.mosquee.controller;

import be.alkaram.mosquee.model.MessageContact;
import be.alkaram.mosquee.repository.MessageContactRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final MessageContactRepository contactRepo;

    public ContactController(MessageContactRepository contactRepo) {
        this.contactRepo = contactRepo;
    }

    /** Envoyer un message de contact */
    @PostMapping
    public ResponseEntity<Map<String, String>> envoyerMessage(@RequestBody MessageContact msg) {
        // Validation basique
        if (msg.getNom() == null || msg.getNom().isBlank()
                || msg.getEmail() == null || msg.getEmail().isBlank()
                || msg.getMessage() == null || msg.getMessage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Nom, email et message sont obligatoires."));
        }

        contactRepo.save(msg);

        // TODO: Envoyer un email de confirmation avec Spring Mail

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "Message envoyé avec succès. JazakAllah khayran !"));
    }
}