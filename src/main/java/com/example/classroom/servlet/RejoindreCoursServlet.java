package com.example.classroom.servlet;

import com.example.classroom.dao.CoursDAO;
import com.example.classroom.dao.EtudiantDAO;
import com.example.classroom.model.Cours;
import com.example.classroom.model.Etudiant;
import com.example.classroom.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/etudiant/rejoindre-cours")
public class RejoindreCoursServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null || !"etudiant".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Etudiant etudiantSession = (Etudiant) session.getAttribute("user");
        String codeClasse = request.getParameter("codeClasse");

        if (codeClasse == null || codeClasse.trim().isEmpty()) {
            // Set error message and redirect back to student accueil
            session.setAttribute("erreurMessageRejoindre", "Le code de la classe ne peut pas être vide.");
            response.sendRedirect(request.getContextPath() + "/etudiant/accueil");
            return;
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CoursDAO coursDAO = new CoursDAO(em);
        // EtudiantDAO is needed to update the student object if using AbstractDAO for Etudiant
        // However, since we are managing Etudiant from session and updating its collections,
        // we might only need to persist the Etudiant object if AbstractDAO doesn't handle cascading merge well.
        // For now, let's assume direct manipulation and then a merge/update on Etudiant.
        // A more robust approach might involve fetching a fresh Etudiant instance from DB.
        EtudiantDAO etudiantDAO = new EtudiantDAO(); // Uses its own EM via AbstractDAO

        try {
            Cours cours = coursDAO.findCoursByCode(codeClasse.trim());

            if (cours == null) {
                session.setAttribute("erreurMessageRejoindre", "Aucun cours trouvé avec le code '" + codeClasse + "'.");
            } else {
                // Fetch the managed Etudiant instance to ensure it's in the current persistence context
                // This is important if EtudiantDAO uses its own EM.
                // Or, pass the 'em' to EtudiantDAO if we refactor it.
                // For simplicity with current EtudiantDAO structure:
                em.getTransaction().begin();
                Etudiant etudiantDB = em.find(Etudiant.class, etudiantSession.getId());

                if (etudiantDB.getCoursInscrits().contains(cours)) {
                    session.setAttribute("infoMessageRejoindre", "Vous êtes déjà inscrit au cours '" + cours.getNom() + "'.");
                } else {
                    etudiantDB.addCours(cours); // This also adds student to cours.getEtudiantsInscrits()
                    // No explicit call to etudiantDAO.update(etudiantDB) is needed if 'em' manages etudiantDB
                    // and transaction commits. If EtudiantDAO was used for update, it would use its own EM.
                    // Since we are using 'em' here, this should be fine.
                    em.getTransaction().commit();
                    session.setAttribute("succesMessageRejoindre", "Vous avez rejoint le cours '" + cours.getNom() + "' avec succès !");
                }
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            session.setAttribute("erreurMessageRejoindre", "Erreur lors de la tentative de rejoindre le cours.");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        response.sendRedirect(request.getContextPath() + "/etudiant/accueil");
    }
}