package be.alkaram.mosquee.repository;

import be.alkaram.mosquee.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findByVisibleTrueOrderByDateEvenementAsc();
}