package com.example.classroom.servlet;

import com.example.classroom.dao.RenduDAO;
import com.example.classroom.model.Etudiant;
import com.example.classroom.model.Rendu;
import com.example.classroom.model.Devoir; // Import Devoir
import com.example.classroom.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/etudiant/devoir-details")
public class EtudiantDevoirDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null || !"etudiant".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Etudiant etudiant = (Etudiant) session.getAttribute("user");
        String renduIdParam = request.getParameter("renduId");

        if (renduIdParam == null || renduIdParam.trim().isEmpty()) {
            // Maybe redirect to student's class page or accueil with an error
            request.setAttribute("erreurMessage", "ID du rendu manquant.");
            // Assuming the student came from a class page, try to get coursId for redirect
            String coursIdParam = request.getParameter("coursId"); // If available
            if (coursIdParam != null && !coursIdParam.isEmpty()) {
                 response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdParam + "&erreur=rendu_id_manquant");
            } else {
                 response.sendRedirect(request.getContextPath() + "/etudiant/accueil?erreur=rendu_id_manquant");
            }
            return;
        }

        int renduId;
        try {
            renduId = Integer.parseInt(renduIdParam);
        } catch (NumberFormatException e) {
            request.setAttribute("erreurMessage", "ID du rendu invalide.");
            String coursIdParam = request.getParameter("coursId");
            if (coursIdParam != null && !coursIdParam.isEmpty()) {
                 response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdParam + "&erreur=rendu_id_invalide");
            } else {
                 response.sendRedirect(request.getContextPath() + "/etudiant/accueil?erreur=rendu_id_invalide");
            }
            return;
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // RenduDAO does not have findById, AbstractDAO does.
            // For consistency with other DAOs not using AbstractDAO, let's assume em.find for now.
            // Or add findById to RenduDAO.
            Rendu rendu = em.find(Rendu.class, renduId);

            if (rendu == null || rendu.getEtudiant().getId() != etudiant.getId()) {
                // Rendu not found or does not belong to the logged-in student
                request.setAttribute("erreurMessage", "Rendu non trouvé ou non autorisé.");
                 String coursIdParam = request.getParameter("coursId"); // For redirect back to class page
                 if (coursIdParam != null && !coursIdParam.isEmpty()) {
                    request.getRequestDispatcher("/etudiant/classe?coursId=" + coursIdParam).forward(request, response);
                 } else {
                    request.getRequestDispatcher("/etudiant/accueil").forward(request, response);
                 }
                return;
            }

            Devoir devoir = rendu.getDevoir(); // Get the associated Devoir

            request.setAttribute("rendu", rendu);
            request.setAttribute("devoir", devoir); // Pass the Devoir object as well
            request.getRequestDispatcher("/jsp/etudiant-devoir.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erreurMessage", "Erreur lors de la récupération des détails du devoir soumis.");
            request.getRequestDispatcher("/etudiant/accueil").forward(request, response); // Fallback redirect
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}