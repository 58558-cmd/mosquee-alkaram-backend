package be.alkaram.mosquee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "config_site")
public class ConfigSite {

    @Id
    private Long id = 1L;

    private BigDecimal montantCollecte;
    private BigDecimal objectif;
    private String telephone;
    private String email;
    private String jumuahTime; // Ex: "13:00"
    private LocalDateTime derniereMaj;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        derniereMaj = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getMontantCollecte() {
        return montantCollecte;
    }

    public void setMontantCollecte(BigDecimal v) {
        this.montantCollecte = v;
    }

    public BigDecimal getObjectif() {
        return objectif;
    }

    public void setObjectif(BigDecimal v) {
        this.objectif = v;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String v) {
        this.telephone = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public String getJumuahTime() {
        return jumuahTime;
    }

    public void setJumuahTime(String v) {
        this.jumuahTime = v;
    }

    public LocalDateTime getDerniereMaj() {
        return derniereMaj;
    }
}