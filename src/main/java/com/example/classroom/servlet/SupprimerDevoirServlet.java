package com.example.classroom.servlet;

import com.example.classroom.dao.DevoirDAO;
import com.example.classroom.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/devoir/delete")
public class SupprimerDevoirServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String devoirIdParam = request.getParameter("id");
        String referer = request.getHeader("Referer"); // Get referer for redirect
        HttpSession session = request.getSession(); // For setting messages

        if (devoirIdParam == null || devoirIdParam.trim().isEmpty()) {
            session.setAttribute("erreurMessageDevoir", "ID de devoir manquant pour la suppression.");
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/jsp/prof-accueil.jsp");
            return;
        }

        int devoirId;
        try {
            devoirId = Integer.parseInt(devoirIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("erreurMessageDevoir", "ID de devoir invalide pour la suppression.");
            response.sendRedirect(referer != null ? referer : request.getContextPath() + "/jsp/prof-accueil.jsp");
            return;
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            DevoirDAO dao = new DevoirDAO(em);
            dao.supprimerDevoir(devoirId); // This no longer manages transaction
            em.getTransaction().commit();
            session.setAttribute("succesMessageDevoir", "Devoir supprimé avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Log error
            session.setAttribute("erreurMessageDevoir", "Erreur lors de la suppression du devoir: " + e.getMessage());
        }
        finally {
            if (em != null && em.isOpen()) em.close();
        }
        response.sendRedirect(referer != null ? referer : request.getContextPath() + "/jsp/prof-accueil.jsp");
    }
}