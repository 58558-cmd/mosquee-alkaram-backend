package be.alkaram.mosquee.repository;

import be.alkaram.mosquee.model.MessageContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageContactRepository extends JpaRepository<MessageContact, Long> {
    List<MessageContact> findByLuFalseOrderByDateEnvoiDesc();
}