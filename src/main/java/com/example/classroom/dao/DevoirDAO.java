package com.example.classroom.dao;

import com.example.classroom.model.Devoir;
// import com.example.classroom.util.JPAUtil; // Not used directly here
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class DevoirDAO {

    private final EntityManager em;

    public DevoirDAO(EntityManager em) {
        this.em = em;
    }

    // Transaction should be managed by the calling servlet
    public void ajouterDevoir(Devoir devoir) {
        em.persist(devoir);
    }

    // Récupérer la liste des devoirs d'un cours donné
    public List<Devoir> getDevoirsParCours(int coursId) {
        TypedQuery<Devoir> query = em.createQuery(
                "SELECT d FROM Devoir d WHERE d.cours.id = :coursId", Devoir.class);
        query.setParameter("coursId", coursId);
        return query.getResultList();
    }

    // Transaction should be managed by the calling servlet
    public void supprimerDevoir(int id) {
        Devoir devoir = em.find(Devoir.class, id);
        if (devoir != null) {
            em.remove(devoir);
        }
    }

    // Récupérer un devoir par son ID
    public Devoir getDevoirById(int id) {
        return em.find(Devoir.class, id);
    }

    public Devoir getDevoirByFichierNom(String fichierNom) {
        TypedQuery<Devoir> query = em.createQuery(
                "SELECT d FROM Devoir d WHERE d.fichierNom = :nom", Devoir.class);
        query.setParameter("nom", fichierNom);
        List<Devoir> resultats = query.getResultList();
        return resultats.isEmpty() ? null : resultats.get(0);
    }
}
