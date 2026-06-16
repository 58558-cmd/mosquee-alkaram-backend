package be.alkaram.mosquee.controller;

import be.alkaram.mosquee.model.ConfigSite;
import be.alkaram.mosquee.model.Don;
import be.alkaram.mosquee.repository.ConfigSiteRepository;
import be.alkaram.mosquee.repository.DonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dons")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://localhost:8080" })
public class DonController {

    private final DonRepository donRepo;
    private final ConfigSiteRepository configRepo;

    public DonController(DonRepository donRepo, ConfigSiteRepository configRepo) {
        this.donRepo = donRepo;
        this.configRepo = configRepo;
    }

    @PostMapping
    public ResponseEntity<Don> creerDon(@RequestBody Don don) {
        don.setStatut("completed");
        return ResponseEntity.ok(donRepo.save(don));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        // Lire depuis ConfigSite (mis à jour par l'admin)
        ConfigSite config = configRepo.findById(1L).orElse(null);

        BigDecimal total = config != null && config.getMontantCollecte() != null
                ? config.getMontantCollecte()
                : new BigDecimal("47678");

        BigDecimal objectif = config != null && config.getObjectif() != null
                ? config.getObjectif()
                : new BigDecimal("258000");

        BigDecimal pourcentage = total
                .divide(objectif, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCollecte", total);
        stats.put("objectif", objectif);
        stats.put("pourcentage", pourcentage);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recents")
    public ResponseEntity<List<Don>> getDerniersDonateurs() {
        List<Don> recents = donRepo.findTop5ByStatutOrderByDateCreationDesc("completed");
        recents.forEach(d -> {
            if (d.isAnonyme()) {
                d.setPrenom("Anonyme");
                d.setNom("");
            }
            d.setEmail("***");
        });
        return ResponseEntity.ok(recents);
    }
}