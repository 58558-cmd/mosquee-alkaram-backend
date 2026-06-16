package be.alkaram.mosquee.controller;

import be.alkaram.mosquee.model.ConfigSite;
import be.alkaram.mosquee.repository.ConfigSiteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigSiteRepository configRepo;

    public ConfigController(ConfigSiteRepository configRepo) {
        this.configRepo = configRepo;
    }

    @GetMapping("/contact")
    public ResponseEntity<Map<String, String>> getContact() {
        ConfigSite config = configRepo.findById(1L).orElse(null);
        Map<String, String> result = new HashMap<>();
        result.put("telephone", config != null && config.getTelephone() != null ? config.getTelephone() : "");
        result.put("email", config != null && config.getEmail() != null ? config.getEmail() : "");
        result.put("jumuahTime", config != null && config.getJumuahTime() != null ? config.getJumuahTime() : "13:00");
        return ResponseEntity.ok(result);
    }
}