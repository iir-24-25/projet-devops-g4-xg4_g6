package com.example.classroom.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cours")
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String code;
    private String description;

    @ManyToMany(mappedBy = "coursInscrits", fetch = FetchType.LAZY)
    private Set<Etudiant> etudiantsInscrits = new HashSet<>();

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Etudiant> getEtudiantsInscrits() {
        return etudiantsInscrits;
    }

    public void setEtudiantsInscrits(Set<Etudiant> etudiantsInscrits) {
        this.etudiantsInscrits = etudiantsInscrits;
    }
}
