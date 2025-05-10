package com.example.classroom.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "devoirs")
public class Devoir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre;
    private String description;

    @Lob
    @Column(name = "fichier_data")
    private byte[] fichierData;  // Fichier stocké en tant que tableau d'octets

    @Column(name = "fichier_nom")
    private String fichierNom;   // Nom du fichier pour l'affichage

    @Temporal(TemporalType.DATE)
    private Date dateLimite;

    @ManyToOne
    @JoinColumn(name = "cours_id")
    private Cours cours;

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public byte[] getFichierData() { return fichierData; }
    public void setFichierData(byte[] fichierData) { this.fichierData = fichierData; }

    public String getFichierNom() { return fichierNom; }
    public void setFichierNom(String fichierNom) { this.fichierNom = fichierNom; }

    public Date getDateLimite() { return dateLimite; }
    public void setDateLimite(Date dateLimite) { this.dateLimite = dateLimite; }

    public Cours getCours() { return cours; }
    public void setCours(Cours cours) { this.cours = cours; }
}
