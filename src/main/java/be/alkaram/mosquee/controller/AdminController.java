package be.alkaram.mosquee.controller;

import be.alkaram.mosquee.model.ConfigSite;
import be.alkaram.mosquee.model.Evenement;
import be.alkaram.mosquee.model.MessageContact;
import be.alkaram.mosquee.repository.ConfigSiteRepository;
import be.alkaram.mosquee.repository.EvenementRepository;
import be.alkaram.mosquee.repository.MessageContactRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ConfigSiteRepository configRepo;
    private final EvenementRepository evenementRepo;
    private final MessageContactRepository messageRepo;

    public AdminController(ConfigSiteRepository configRepo,
            EvenementRepository evenementRepo,
            MessageContactRepository messageRepo) {
        this.configRepo = configRepo;
        this.evenementRepo = evenementRepo;
        this.messageRepo = messageRepo;
    }

    private ConfigSite getOrCreateConfig() {
        return configRepo.findById(1L).orElseGet(() -> {
            ConfigSite c = new ConfigSite();
            c.setMontantCollecte(new BigDecimal("47678"));
            c.setObjectif(new BigDecimal("258000"));
            c.setJumuahTime("13:00");
            return configRepo.save(c);
        });
    }

    @GetMapping("/config")
    public ResponseEntity<ConfigSite> getConfig() {
        return ResponseEntity.ok(getOrCreateConfig());
    }

    @PostMapping("/config/montant")
    public ResponseEntity<ConfigSite> updateMontant(@RequestBody Map<String, Object> body) {
        ConfigSite config = getOrCreateConfig();
        if (body.containsKey("valeur") && body.get("valeur") != null && !body.get("valeur").toString().isBlank()) {
            String mode = (String) body.get("mode");
            BigDecimal valeur = new BigDecimal(body.get("valeur").toString());
            if ("ajouter".equals(mode)) {
                BigDecimal actuel = config.getMontantCollecte() != null ? config.getMontantCollecte() : BigDecimal.ZERO;
                config.setMontantCollecte(actuel.add(valeur));
            } else {
                config.setMontantCollecte(valeur);
            }
        }
        if (body.containsKey("objectif") && body.get("objectif") != null
                && !body.get("objectif").toString().isBlank()) {
            config.setObjectif(new BigDecimal(body.get("objectif").toString()));
        }
        return ResponseEntity.ok(configRepo.save(config));
    }

    @PostMapping("/config/contact")
    public ResponseEntity<ConfigSite> updateContact(@RequestBody Map<String, String> body) {
        ConfigSite config = getOrCreateConfig();
        if (body.containsKey("telephone"))
            config.setTelephone(body.get("telephone"));
        if (body.containsKey("email"))
            config.setEmail(body.get("email"));
        if (body.containsKey("jumuahTime"))
            config.setJumuahTime(body.get("jumuahTime"));
        return ResponseEntity.ok(configRepo.save(config));
    }

    @GetMapping("/evenements")
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        return ResponseEntity.ok(evenementRepo.findAll());
    }

    @PostMapping("/evenements")
    public ResponseEntity<Evenement> createEvenement(@RequestBody Evenement ev) {
        return ResponseEntity.ok(evenementRepo.save(ev));
    }

    @PutMapping("/evenements/{id}")
    public ResponseEntity<Evenement> updateEvenement(@PathVariable Long id, @RequestBody Evenement ev) {
        return evenementRepo.findById(id).map(existing -> {
            existing.setTitre(ev.getTitre());
            existing.setDescription(ev.getDescription());
            existing.setTag(ev.getTag());
            existing.setTagColor(ev.getTagColor());
            existing.setDateEvenement(ev.getDateEvenement());
            existing.setLieu(ev.getLieu());
            existing.setHeure(ev.getHeure());
            existing.setVisible(ev.isVisible());
            return ResponseEntity.ok(evenementRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/evenements/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messages")
    public ResponseEntity<List<MessageContact>> getMessages() {
        return ResponseEntity.ok(messageRepo.findAll());
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}