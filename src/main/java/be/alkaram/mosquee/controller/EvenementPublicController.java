package be.alkaram.mosquee.controller;

import be.alkaram.mosquee.model.Evenement;
import be.alkaram.mosquee.repository.EvenementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
public class EvenementPublicController {

    private final EvenementRepository evenementRepo;

    public EvenementPublicController(EvenementRepository evenementRepo) {
        this.evenementRepo = evenementRepo;
    }

    @GetMapping("/public")
    public ResponseEntity<List<Evenement>> getPublic() {
        return ResponseEntity.ok(evenementRepo.findByVisibleTrueOrderByDateEvenementAsc());
    }
}