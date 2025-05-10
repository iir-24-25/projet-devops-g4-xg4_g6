package com.example.classroom.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "etudiants")
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 128) // Store hashed password, adjust length as needed for Base64 SHA-256
    private String password; // This will store the hashed password

    @Column(nullable = false)
    @Lob // For byte array, though for small salts, not strictly necessary but good practice
    private byte[] passwordSalt;

    private String telephone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "etudiant_cours",
            joinColumns = @JoinColumn(name = "etudiant_id"),
            inverseJoinColumns = @JoinColumn(name = "cours_id")
    )
    private Set<Cours> coursInscrits = new HashSet<>();

    // --- Constructeurs ---
    public Etudiant() {}

    // Constructor will be updated in RegisterServlet to handle hashing
    public Etudiant(String nom, String prenom, String email, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // --- Getters et Setters ---
    public int getId() { return id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; } // Hashed password

    public byte[] getPasswordSalt() { return passwordSalt; }
    public void setPasswordSalt(byte[] passwordSalt) { this.passwordSalt = passwordSalt; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Set<Cours> getCoursInscrits() {
        return coursInscrits;
    }

    public void setCoursInscrits(Set<Cours> coursInscrits) {
        this.coursInscrits = coursInscrits;
    }

    public void addCours(Cours cours) {
        this.coursInscrits.add(cours);
        cours.getEtudiantsInscrits().add(this);
    }

    public void removeCours(Cours cours) {
        this.coursInscrits.remove(cours);
        cours.getEtudiantsInscrits().remove(this);
    }
}
