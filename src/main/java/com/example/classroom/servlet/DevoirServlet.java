package com.example.classroom.servlet;

import com.example.classroom.dao.CoursDAO;
import com.example.classroom.dao.DevoirDAO;
import com.example.classroom.model.Cours;
import com.example.classroom.model.Devoir;
import com.example.classroom.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths; // Added import
import java.sql.Date; // Keep this for Date.valueOf

@WebServlet("/devoir")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5)
public class DevoirServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String coursIdParam = request.getParameter("coursId");
        if (coursIdParam == null || coursIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/jsp/prof-accueil.jsp?erreur=coursId_manquant");
            return;
        }
        int coursId;
        try {
            coursId = Integer.parseInt(coursIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/jsp/prof-accueil.jsp?erreur=coursId_invalide");
            return;
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CoursDAO coursDAO = new CoursDAO(em);
            DevoirDAO devoirDAO = new DevoirDAO(em);

            Cours cours = coursDAO.getCoursById(coursId);
            if (cours == null) {
                 response.sendRedirect(request.getContextPath() + "/jsp/prof-accueil.jsp?erreur=cours_non_trouve");
                 return;
            }
            request.setAttribute("cours", cours);
            request.setAttribute("devoirs", devoirDAO.getDevoirsParCours(coursId));

            request.getRequestDispatcher("/jsp/classe-devoirs.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace(); 
            request.setAttribute("erreurMessage", "Erreur lors de la récupération des devoirs.");
            request.getRequestDispatcher("/jsp/prof-accueil.jsp").forward(request, response);
        }
        finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        String coursIdParam = request.getParameter("coursId"); 
        int coursId = 0; 
        try {
            coursId = Integer.parseInt(coursIdParam); 
            String titre = request.getParameter("titre");
            String description = request.getParameter("description");
            String dateLimiteParam = request.getParameter("date_limite");

            if (titre == null || titre.trim().isEmpty() || dateLimiteParam == null || dateLimiteParam.trim().isEmpty()) {
                request.setAttribute("erreurFormDevoir", "Titre et date limite sont requis.");
                // Re-fetch data for the form page if forwarding
                // Need to set cours attribute again for the forwarded request
                CoursDAO coursDAOForError = new CoursDAO(em); // Create new DAO with current EM for error path
                DevoirDAO devoirDAOForError = new DevoirDAO(em);
                request.setAttribute("cours", coursDAOForError.getCoursById(coursId));
                request.setAttribute("devoirs", devoirDAOForError.getDevoirsParCours(coursId));
                request.getRequestDispatcher("/jsp/classe-devoirs.jsp").forward(request, response);
                return;
            }
            Date dateLimite = Date.valueOf(dateLimiteParam);


            Part filePart = request.getPart("fichier");
            String fileName = null;
            byte[] fileData = null;

            if (filePart != null && filePart.getSize() > 0) {
                fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                try (InputStream is = filePart.getInputStream()) {
                    fileData = is.readAllBytes();
                }
            }

            em.getTransaction().begin();

            Devoir devoir = new Devoir();
            devoir.setTitre(titre);
            devoir.setDescription(description);
            devoir.setDateLimite(dateLimite);
            devoir.setFichierNom(fileName);
            devoir.setFichierData(fileData);

            Cours cours = em.find(Cours.class, coursId);
            if (cours == null) {
                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                request.setAttribute("erreurFormDevoir", "Cours associé non trouvé.");
                // Re-fetch data for the form page
                CoursDAO coursDAOForError = new CoursDAO(em);
                DevoirDAO devoirDAOForError = new DevoirDAO(em);
                request.setAttribute("cours", coursDAOForError.getCoursById(coursId)); // coursId might be 0 if parsing failed earlier, handle this
                request.setAttribute("devoirs", devoirDAOForError.getDevoirsParCours(coursId));
                request.getRequestDispatcher("/jsp/classe-devoirs.jsp").forward(request, response);
                return;
            }
            devoir.setCours(cours);

            DevoirDAO dao = new DevoirDAO(em);
            dao.ajouterDevoir(devoir); 

            em.getTransaction().commit();
            HttpSession sessionHttp = request.getSession();
            sessionHttp.setAttribute("succesMessageDevoir", "Devoir '" + titre + "' ajouté avec succès!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); 
            request.setAttribute("erreurFormDevoir", "Erreur lors de l'ajout du devoir: " + e.getMessage());
            // To re-populate the form correctly on error, forward instead of redirect
            // and ensure all necessary attributes for classe-devoirs.jsp are set.
            try { // Nested try-catch for fetching data for error display
                CoursDAO coursDAOForError = new CoursDAO(em);
                DevoirDAO devoirDAOForError = new DevoirDAO(em);
                request.setAttribute("cours", coursDAOForError.getCoursById(coursId)); // coursId might be 0 if parsing failed earlier
                request.setAttribute("devoirs", devoirDAOForError.getDevoirsParCours(coursId));
                request.getRequestDispatcher("/jsp/classe-devoirs.jsp").forward(request, response);
            } catch (Exception ex) {
                ex.printStackTrace(); // Error during error handling
                response.sendRedirect(request.getContextPath() + "/devoir?coursId=" + coursIdParam + "&erreurAjout=true&fatal=true");
            }
            return; 
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
        response.sendRedirect(request.getContextPath() + "/devoir?coursId=" + coursIdParam);
    }
}
