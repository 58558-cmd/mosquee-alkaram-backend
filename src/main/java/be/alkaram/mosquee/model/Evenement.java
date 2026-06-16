package be.alkaram.mosquee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evenements")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(length = 1000)
    private String description;

    private String tag; // "Conférence", "Collecte", "Cours", etc.
    private String tagColor;
    private LocalDateTime dateEvenement;
    private String lieu;
    private String heure;
    private boolean visible;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (visible == false) visible = true;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getTagColor() { return tagColor; }
    public void setTagColor(String tagColor) { this.tagColor = tagColor; }
    public LocalDateTime getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(LocalDateTime dateEvenement) { this.dateEvenement = dateEvenement; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public String getHeure() { return heure; }
    public void setHeure(String heure) { this.heure = heure; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public LocalDateTime getDateCreation() { return dateCreation; }
}