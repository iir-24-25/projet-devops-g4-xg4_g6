package com.example.classroom.servlet;

import com.example.classroom.dao.RenduDAO;
import com.example.classroom.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/evaluer-devoir")
public class EvaluerDevoirServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String renduIdParam = request.getParameter("renduId");
        String noteParam = request.getParameter("note");
        String commentaire = request.getParameter("commentaire");
        String referer = request.getHeader("Referer"); // Get referer for redirect

        int renduId;
        int note;

        try {
            renduId = Integer.parseInt(renduIdParam);
            note = Integer.parseInt(noteParam);
        } catch (NumberFormatException e) {
            // Handle invalid input, perhaps set an error message in session and redirect
            e.printStackTrace(); // Log error
            // It might be better to redirect with an error query parameter or session attribute
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/jsp/prof-accueil.jsp");
            return;
        }


        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        int devoirId = -1;
        try {
            em.getTransaction().begin();
            RenduDAO dao = new RenduDAO(em);
            dao.evaluerRendu(renduId, note, commentaire);
            // Récupérer le devoirId à partir du rendu
            com.example.classroom.model.Rendu rendu = em.find(com.example.classroom.model.Rendu.class, renduId);
            if (rendu != null && rendu.getDevoir() != null) {
                devoirId = rendu.getDevoir().getId();
            }
            em.getTransaction().commit();
            // Message de succès
            request.getSession().setAttribute("successMessage", "La note a été enregistrée avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Log error
            // Optionally, set an error message in session
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        // Redirection vers la page de détails du devoir avec le message de succès
        if (devoirId != -1) {
            response.sendRedirect(request.getContextPath() + "/devoir/details?id=" + devoirId);
        } else {
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/jsp/prof-accueil.jsp");
        }
    }
}
