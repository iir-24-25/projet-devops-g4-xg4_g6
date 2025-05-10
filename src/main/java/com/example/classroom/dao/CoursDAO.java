package com.example.classroom.dao;

import com.example.classroom.model.Cours;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CoursDAO {

    private final EntityManager em;

    public CoursDAO(EntityManager em) {
        this.em = em;
    }

    // Transaction should be managed by the calling servlet
    public void ajouterCours(Cours cours) {
        em.persist(cours);
    }

    public List<Cours> getTousLesCours() {
        TypedQuery<Cours> query = em.createQuery("SELECT c FROM Cours c", Cours.class);
        return query.getResultList();
    }

    public Cours getCoursById(int id) {
        try {
            return em.find(Cours.class, id);
        } catch (Exception e) {
            // Idéalement, loguer l'erreur avec un framework de logging
            e.printStackTrace(); // Pour le débogage
            return null;
        }
    }

    public Cours findCoursByCode(String code) {
        TypedQuery<Cours> query = em.createQuery("SELECT c FROM Cours c WHERE c.code = :code", Cours.class);
        query.setParameter("code", code);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Aucun cours trouvé avec ce code
        }
    }
}
