package be.alkaram.mosquee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages_contact")
public class MessageContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;
    private String telephone;
    private String sujet;

    @Column(length = 2000)
    private String message;

    private LocalDateTime dateEnvoi;
    private boolean lu;

    @PrePersist
    protected void onCreate() {
        dateEnvoi = LocalDateTime.now();
        lu = false;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
}