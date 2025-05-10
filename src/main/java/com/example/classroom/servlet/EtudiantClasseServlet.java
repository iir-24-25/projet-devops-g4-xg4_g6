package com.example.classroom.servlet;

import com.example.classroom.dao.CoursDAO;
import com.example.classroom.dao.DevoirDAO;
import com.example.classroom.dao.RenduDAO;
import com.example.classroom.model.Cours;
import com.example.classroom.model.Devoir;
import com.example.classroom.model.Etudiant;
import com.example.classroom.model.Rendu;
import com.example.classroom.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap; // Added for explicit map creation
import java.util.List;
import java.util.Map;
// import java.util.stream.Collectors; // No longer needed for Collectors.toMap

@WebServlet("/etudiant/classe")
public class EtudiantClasseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null || !"etudiant".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Etudiant etudiantFromSession = (Etudiant) session.getAttribute("user");
        String coursIdParam = request.getParameter("coursId");

        if (coursIdParam == null || coursIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/etudiant/accueil?erreur=coursId_manquant");
            return;
        }

        int coursId;
        try {
            coursId = Integer.parseInt(coursIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/etudiant/accueil?erreur=coursId_invalide");
            return;
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Ensure the Etudiant object is managed in the current persistence context
            Etudiant managedEtudiant = em.find(Etudiant.class, etudiantFromSession.getId());
            if (managedEtudiant == null) {
                // This case should ideally not happen if user is properly logged in
                session.invalidate(); // Log out user as their session data is inconsistent
                response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?erreur=session_invalide");
                return;
            }

            CoursDAO coursDAO = new CoursDAO(em);
            DevoirDAO devoirDAO = new DevoirDAO(em);
            RenduDAO renduDAO = new RenduDAO(em);

            Cours cours = coursDAO.getCoursById(coursId);
            if (cours == null) {
                request.setAttribute("erreurMessage", "Cours non trouvé.");
                request.getRequestDispatcher("/jsp/etudiant-accueil.jsp").forward(request, response);
                return;
            }

            List<Devoir> devoirs = devoirDAO.getDevoirsParCours(coursId);
            Map<Integer, Rendu> rendusMap = new HashMap<>();

            if (devoirs != null) {
                for (Devoir devoir : devoirs) {
                    Rendu rendu = renduDAO.findRenduByEtudiantAndDevoir(managedEtudiant.getId(), devoir.getId());
                    if (rendu != null) {
                        rendusMap.put(devoir.getId(), rendu);
                    }
                    // If rendu is null, it simply won't be in the map, JSP will handle missing entry
                }
            }

            request.setAttribute("cours", cours);
            request.setAttribute("devoirs", devoirs);
            request.setAttribute("rendusMap", rendusMap);

            request.getRequestDispatcher("/jsp/etudiant-classe.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erreurMessage", "Erreur lors de la récupération des détails de la classe.");
            // Forwarding to etudiant-accueil.jsp which can display this erreurMessage
            request.getRequestDispatcher("/etudiant/accueil").forward(request, response);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}