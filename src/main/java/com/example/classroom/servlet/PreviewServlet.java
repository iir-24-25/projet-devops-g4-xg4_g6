package com.example.classroom.servlet;

import com.example.classroom.model.Devoir;
import com.example.classroom.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/preview")
public class PreviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int devoirId = Integer.parseInt(request.getParameter("devoirId"));

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            Devoir devoir = em.find(Devoir.class, devoirId);
            if (devoir == null || devoir.getFichierData() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier introuvable.");
                return;
            }

            String fileName = devoir.getFichierNom();
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

            // Détecter le type MIME
            String contentType = switch (extension) {
                case "pdf" -> "application/pdf";
                case "doc", "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                default -> "application/octet-stream";
            };

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            response.setContentLength(devoir.getFichierData().length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(devoir.getFichierData());
            }
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
