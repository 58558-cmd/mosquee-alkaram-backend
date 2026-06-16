package be.alkaram.mosquee.controller;

import be.alkaram.mosquee.model.ConfigSite;
import be.alkaram.mosquee.model.Don;
import be.alkaram.mosquee.repository.ConfigSiteRepository;
import be.alkaram.mosquee.repository.DonRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentRetrieveParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174", "http://localhost:8080" })
public class StripeController {

    private final DonRepository donRepo;
    private final ConfigSiteRepository configRepo;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    public StripeController(DonRepository donRepo, ConfigSiteRepository configRepo) {
        this.donRepo = donRepo;
        this.configRepo = configRepo;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        return ResponseEntity.ok(Map.of("publicKey", stripePublicKey));
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> body) {
        try {
            Stripe.apiKey = stripeSecretKey;

            int montant = Integer.parseInt(body.get("montant").toString());
            String prenom = (String) body.getOrDefault("prenom", "");
            String nom = (String) body.getOrDefault("nom", "");
            String email = (String) body.getOrDefault("email", "");
            boolean anonyme = Boolean.parseBoolean(body.getOrDefault("anonyme", "false").toString());
            String frequence = (String) body.getOrDefault("frequence", "once");

            // Sauvegarder don en attente
            Don don = new Don();
            don.setPrenom(prenom);
            don.setNom(nom);
            don.setEmail(email);
            don.setMontant(new BigDecimal(montant));
            don.setAnonyme(anonyme);
            don.setFrequence(frequence);
            don.setStatut("pending");
            Don saved = donRepo.save(don);

            // Créer Payment Intent avec toutes les méthodes de paiement
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) montant * 100)
                    .setCurrency("eur")
                    .setReceiptEmail(email.isEmpty() ? null : email)
                    .setDescription("Don — Mosquée Al-Karam, Forest")
                    .putMetadata("don_id", String.valueOf(saved.getId()))
                    .putMetadata("prenom", prenom)
                    .putMetadata("nom", nom)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return ResponseEntity.ok(Map.of(
                    "clientSecret", intent.getClientSecret(),
                    "donId", saved.getId(),
                    "publicKey", stripePublicKey));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erreur", e.getMessage()));
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> body) {
        try {
            Stripe.apiKey = stripeSecretKey;
            String paymentIntentId = (String) body.get("paymentIntentId");
            Long donId = Long.parseLong(body.get("donId").toString());

            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            if ("succeeded".equals(intent.getStatus())) {
                donRepo.findById(donId).ifPresent(don -> {
                    don.setStatut("completed");
                    don.setStripeSessionId(paymentIntentId);
                    donRepo.save(don);
                });

                // Mettre à jour le montant collecté dans ConfigSite
                ConfigSite config = configRepo.findById(1L).orElseGet(ConfigSite::new);
                BigDecimal actuel = config.getMontantCollecte() != null
                        ? config.getMontantCollecte()
                        : new BigDecimal("47678");
                BigDecimal montantDon = donRepo.findById(donId)
                        .map(Don::getMontant)
                        .orElse(BigDecimal.ZERO);
                config.setMontantCollecte(actuel.add(montantDon));
                configRepo.save(config);

                return ResponseEntity.ok(Map.of("statut", "succeeded"));
            }

            return ResponseEntity.ok(Map.of("statut", intent.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erreur", e.getMessage()));
        }
    }
}