package com.example.classroom.dao;

import com.example.classroom.model.Rendu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RenduDAO {
    private final EntityManager em;

    public RenduDAO(EntityManager em) {
        this.em = em;
    }

    public List<Rendu> getRendusParDevoir(int devoirId) {
        TypedQuery<Rendu> query = em.createQuery(
                "SELECT r FROM Rendu r WHERE r.devoir.id = :devoirId", Rendu.class);
        query.setParameter("devoirId", devoirId);
        return query.getResultList();
    }

    // Transaction should be managed by the calling servlet
    public void evaluerRendu(int renduId, int note, String commentaire) {
        Rendu rendu = em.find(Rendu.class, renduId);
        if (rendu != null) {
            rendu.setNote(note);
            rendu.setCommentaire(commentaire);
            // em.merge(rendu); // Optional: merge if 'rendu' could be detached, but find should keep it managed.
        }
    }

    public Rendu findRenduByEtudiantAndDevoir(int etudiantId, int devoirId) {
        TypedQuery<Rendu> query = em.createQuery(
                "SELECT r FROM Rendu r WHERE r.etudiant.id = :etudiantId AND r.devoir.id = :devoirId", Rendu.class);
        query.setParameter("etudiantId", etudiantId);
        query.setParameter("devoirId", devoirId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Aucun rendu trouvé pour cet étudiant et ce devoir
        }
    }

    // Transaction should be managed by the calling servlet
    public void create(Rendu rendu) {
        em.persist(rendu);
    }
}
