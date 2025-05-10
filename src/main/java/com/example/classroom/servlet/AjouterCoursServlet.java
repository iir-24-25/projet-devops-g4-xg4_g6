package com.example.classroom.servlet;

import com.example.classroom.dao.CoursDAO;
import com.example.classroom.model.Cours;
import com.example.classroom.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/ajouter-cours")
public class AjouterCoursServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nom = request.getParameter("nom");
        String description = request.getParameter("description");
        // Basic validation
        if (nom == null || nom.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            // Consider sending an error back to the form or a dedicated error page
            response.sendRedirect(request.getContextPath() + "/jsp/prof-accueil.jsp?erreur=champs_manquants");
            return;
        }
        String code = genererCode();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Cours cours = new Cours();
            cours.setNom(nom);
            cours.setCode(code);
            cours.setDescription(description);

            CoursDAO dao = new CoursDAO(em);
            dao.ajouterCours(cours);

            em.getTransaction().commit();
            // Optionally, set a success message in session
            HttpSession session = request.getSession();
            session.setAttribute("succesMessageCours", "Cours '" + nom + "' ajouté avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Log error
            // Optionally, set an error message in session
            HttpSession session = request.getSession();
            session.setAttribute("erreurMessageCours", "Erreur lors de l'ajout du cours: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        response.sendRedirect(request.getContextPath() + "/jsp/prof-accueil.jsp");
    }

    private String genererCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }
}
