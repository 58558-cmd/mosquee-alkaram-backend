package be.alkaram.mosquee.repository;

import be.alkaram.mosquee.model.Don;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonRepository extends JpaRepository<Don, Long> {

    List<Don> findByStatutOrderByDateCreationDesc(String statut);

    Optional<Don> findByStripeSessionId(String stripeSessionId);

    @Query("SELECT SUM(d.montant) FROM Don d WHERE d.statut = 'completed'")
    BigDecimal sumMontantCompleted();

    @Query("SELECT COUNT(d) FROM Don d WHERE d.statut = 'completed'")
    long countCompleted();

    List<Don> findTop5ByStatutOrderByDateCreationDesc(String statut);
}