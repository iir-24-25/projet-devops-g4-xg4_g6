package com.example.classroom.servlet;

import com.example.classroom.dao.DevoirDAO;
import com.example.classroom.model.Devoir;
import com.example.classroom.model.Rendu; // Added import
import com.example.classroom.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String devoirIdParam = request.getParameter("devoirId");
        String fichierNomParam = request.getParameter("filename"); // Used for Devoir by filename or Rendu by its stored filename
        String renduIdParam = request.getParameter("renduId");
        // boolean isRenduDownload = "true".equalsIgnoreCase(request.getParameter("isRendu")); // No longer strictly needed if using renduId

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        byte[] fileData = null;
        String fileNameForDownload = null;

        try {
            if (renduIdParam != null) {
                int renduId = Integer.parseInt(renduIdParam);
                Rendu rendu = em.find(Rendu.class, renduId);
                if (rendu != null && rendu.getFichierData() != null) {
                    fileData = rendu.getFichierData();
                    fileNameForDownload = rendu.getFichierNom();
                }
            } else if (devoirIdParam != null) {
                int devoirId = Integer.parseInt(devoirIdParam);
                Devoir devoir = em.find(Devoir.class, devoirId);
                if (devoir != null && devoir.getFichierData() != null) {
                    fileData = devoir.getFichierData();
                    fileNameForDownload = devoir.getFichierNom();
                }
            } else if (fichierNomParam != null && !fichierNomParam.isEmpty()) {
                // This case was originally for Devoir by filename.
                // If we need to download Rendu by filename (less ideal as names might not be unique across all rendus),
                // we would need a RenduDAO.findRenduByFichierNom (which doesn't exist and is problematic).
                // For now, this branch remains for Devoir files if identified by filename.
                DevoirDAO devoirDAO = new DevoirDAO(em);
                Devoir devoir = devoirDAO.getDevoirByFichierNom(fichierNomParam);
                if (devoir != null && devoir.getFichierData() != null) {
                    fileData = devoir.getFichierData();
                    fileNameForDownload = devoir.getFichierNom();
                }
            }

            if (fileData != null && fileNameForDownload != null) {
                String mimeType = getServletContext().getMimeType(fileNameForDownload);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                response.setContentType(mimeType);
                response.setContentLength(fileData.length);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameForDownload + "\"");

                try (InputStream fileInputStream = new java.io.ByteArrayInputStream(fileData);
                     OutputStream outStream = response.getOutputStream()) {

                    byte[] buffer = new byte[4096]; // Increased buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier introuvable.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide.");
        } 
        catch (Exception e) {
            e.printStackTrace(); // Log the full error
            throw new ServletException("Erreur lors du téléchargement du fichier: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
