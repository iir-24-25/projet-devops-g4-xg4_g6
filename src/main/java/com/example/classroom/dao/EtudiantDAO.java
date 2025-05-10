package com.example.classroom.dao;

import com.example.classroom.model.Cours;
import com.example.classroom.model.Etudiant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException; // Added for explicit handling
import java.util.Collections;
import java.util.Set;

public class EtudiantDAO extends AbstractDAO<Etudiant> {
    public EtudiantDAO() {
        super(Etudiant.class);
    }

    public Set<Cours> findCoursInscritsByEtudiantId(int etudiantId) {
        EntityManager em = emf.createEntityManager();
        try {
            Etudiant etudiant = em.find(Etudiant.class, etudiantId);
            if (etudiant != null) {
                // Initialize the collection if it's LAZY fetched
                // and the transaction is not active or entity is detached
                // For simplicity here, direct access, assuming session is active
                // or EAGER fetch (though we set LAZY).
                // A more robust way for LAZY is to use a query or ensure session scope.
                // For now, let's rely on the session being open or use a query if issues arise.
                // To be safe and explicit with LAZY fetching:
                Etudiant mergedEtudiant = em.merge(etudiant); // Re-attach if detached
                mergedEtudiant.getCoursInscrits().size(); // Trigger loading
                return mergedEtudiant.getCoursInscrits();
            }
            return Collections.emptySet();
        } catch (NoResultException e) {
            return Collections.emptySet(); // Student not found
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
