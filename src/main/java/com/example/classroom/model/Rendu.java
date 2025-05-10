package com.example.classroom.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rendus")
public class Rendu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    // Changed from fichierPath to fichierData
    @Lob
    @Column(name = "fichier_data") // Explicit column name for clarity
    private byte[] fichierData;

    @Column(name = "fichier_nom") // To store the original name of the uploaded file
    private String fichierNom;

    @Temporal(TemporalType.TIMESTAMP) // Changed to TIMESTAMP to include time of submission
    private Date dateRendu;

    private Integer note;
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devoir_id", nullable = false)
    private Devoir devoir;

    // Constructors
    public Rendu() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public byte[] getFichierData() {
        return fichierData;
    }

    public void setFichierData(byte[] fichierData) {
        this.fichierData = fichierData;
    }

    public String getFichierNom() {
        return fichierNom;
    }

    public void setFichierNom(String fichierNom) {
        this.fichierNom = fichierNom;
    }

    public Date getDateRendu() {
        return dateRendu;
    }

    public void setDateRendu(Date dateRendu) {
        this.dateRendu = dateRendu;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Devoir getDevoir() {
        return devoir;
    }

    public void setDevoir(Devoir devoir) {
        this.devoir = devoir;
    }
}
