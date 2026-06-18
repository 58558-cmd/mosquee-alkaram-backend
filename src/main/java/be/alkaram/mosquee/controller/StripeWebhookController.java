package be.alkaram.mosquee.controller;

import be.alkaram.mosquee.model.ConfigSite;
import be.alkaram.mosquee.model.Don;
import be.alkaram.mosquee.repository.ConfigSiteRepository;
import be.alkaram.mosquee.repository.DonRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private final DonRepository donRepo;
    private final ConfigSiteRepository configRepo;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    public StripeWebhookController(DonRepository donRepo, ConfigSiteRepository configRepo) {
        this.donRepo = donRepo;
        this.configRepo = configRepo;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        // Si pas de webhook secret configuré, ignorer
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.ok("No webhook secret configured");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        // Traiter les paiements réussis (don unique)
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElse(null);

            if (intent != null) {
                String donIdStr = intent.getMetadata().get("don_id");
                if (donIdStr != null) {
                    Long donId = Long.parseLong(donIdStr);
                    donRepo.findById(donId).ifPresent(don -> {
                        if (!"completed".equals(don.getStatut())) {
                            don.setStatut("completed");
                            don.setStripeSessionId(intent.getId());
                            donRepo.save(don);
                            updateCagnotte(don);
                        }
                    });
                }
            }
        }

        // Traiter les paiements réussis (don mensuel — première facture et
        // renouvellements)
        if ("invoice.payment_succeeded".equals(event.getType())) {
            Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                    .getObject().orElse(null);

            if (invoice != null && invoice.getSubscription() != null) {
                String subscriptionId = invoice.getSubscription();
                donRepo.findByStripeSessionId(subscriptionId).ifPresent(don -> {
                    // Pour le premier paiement, marquer completed et créditer la cagnotte
                    if (!"completed".equals(don.getStatut())) {
                        don.setStatut("completed");
                        donRepo.save(don);
                        updateCagnotte(don);
                    } else {
                        // Renouvellement mensuel : créditer à nouveau la cagnotte
                        updateCagnotte(don);
                    }
                });
            }
        }

        return ResponseEntity.ok("OK");
    }

    private void updateCagnotte(Don don) {
        ConfigSite config = configRepo.findById(1L).orElseGet(ConfigSite::new);
        BigDecimal actuel = config.getMontantCollecte() != null
                ? config.getMontantCollecte()
                : BigDecimal.ZERO;
        config.setMontantCollecte(actuel.add(don.getMontant()));
        configRepo.save(config);
    }
}