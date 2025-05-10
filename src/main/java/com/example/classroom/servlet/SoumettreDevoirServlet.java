package com.example.classroom.servlet;

import com.example.classroom.dao.DevoirDAO;
import com.example.classroom.dao.RenduDAO;
import com.example.classroom.model.Devoir;
import com.example.classroom.model.Etudiant;
import com.example.classroom.model.Rendu;
import com.example.classroom.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

// import java.io.File; // No longer needed for filesystem saving
import java.io.IOException;
import java.io.InputStream;
// import java.nio.file.Files; // No longer needed
import java.nio.file.Paths; // Still useful for getting filename from Part
// import java.nio.file.StandardCopyOption; // No longer needed
import java.util.Date;
// import java.util.UUID; // No longer needed if not creating unique filenames for filesystem

@WebServlet("/etudiant/soumettre-devoir")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 15   // 15 MB
)
public class SoumettreDevoirServlet extends HttpServlet {

    // private static final String UPLOAD_DIR_BASE = "uploads" + File.separator + "rendus"; // No longer needed

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String coursIdForRedirect = request.getParameter("coursId"); 

        if (session == null || session.getAttribute("user") == null || !"etudiant".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Etudiant etudiant = (Etudiant) session.getAttribute("user");
        String devoirIdParam = request.getParameter("devoirId");
        Part filePart = request.getPart("fichierRendu");

        if (devoirIdParam == null || filePart == null || filePart.getSize() == 0) {
            session.setAttribute("erreurMessageSoumission", "Veuillez sélectionner un devoir et un fichier.");
            response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdForRedirect);
            return;
        }

        int devoirId;
        try {
            devoirId = Integer.parseInt(devoirIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("erreurMessageSoumission", "ID de devoir invalide.");
            response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdForRedirect);
            return;
        }

        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        byte[] fileData;
        try (InputStream fileContent = filePart.getInputStream()) {
            fileData = fileContent.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            session.setAttribute("erreurMessageSoumission", "Erreur lors de la lecture du fichier uploadé.");
            response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdForRedirect);
            return;
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            RenduDAO renduDAO = new RenduDAO(em);
            DevoirDAO devoirDAO = new DevoirDAO(em);
            
            Devoir devoir = devoirDAO.getDevoirById(devoirId);
            Etudiant managedEtudiant = em.find(Etudiant.class, etudiant.getId()); 

            if (devoir == null || managedEtudiant == null) {
                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                session.setAttribute("erreurMessageSoumission", "Devoir ou étudiant non trouvé.");
                response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdForRedirect);
                return;
            }
            
            if (renduDAO.findRenduByEtudiantAndDevoir(managedEtudiant.getId(), devoir.getId()) != null) {
                 if(em.getTransaction().isActive()) em.getTransaction().rollback();
                 session.setAttribute("infoMessageSoumission", "Vous avez déjà soumis un rendu pour ce devoir.");
                 response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdForRedirect);
                 return;
            }

            Rendu rendu = new Rendu();
            rendu.setEtudiant(managedEtudiant);
            rendu.setDevoir(devoir);
            rendu.setFichierData(fileData); // Set byte array
            rendu.setFichierNom(originalFileName); // Set original filename
            rendu.setDateRendu(new Date());

            renduDAO.create(rendu);

            em.getTransaction().commit();
            session.setAttribute("succesMessageSoumission", "Devoir soumis avec succès !");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            session.setAttribute("erreurMessageSoumission", "Erreur lors de la soumission du devoir: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        response.sendRedirect(request.getContextPath() + "/etudiant/classe?coursId=" + coursIdForRedirect);
    }
}