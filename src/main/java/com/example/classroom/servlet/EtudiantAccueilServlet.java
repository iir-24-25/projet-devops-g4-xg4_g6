package com.example.classroom.servlet;

import com.example.classroom.dao.EtudiantDAO;
import com.example.classroom.model.Cours;
import com.example.classroom.model.Etudiant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;
import java.util.Collections;

@WebServlet("/etudiant/accueil")
public class EtudiantAccueilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // false = do not create new session if one doesn't exist

        if (session == null || session.getAttribute("user") == null || !"etudiant".equals(session.getAttribute("role"))) {
            // User not logged in as student, redirect to login
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        Etudiant etudiant = (Etudiant) session.getAttribute("user");
        EtudiantDAO etudiantDAO = new EtudiantDAO();
        Set<Cours> coursInscrits = Collections.emptySet();

        try {
            coursInscrits = etudiantDAO.findCoursInscritsByEtudiantId(etudiant.getId());
        } catch (Exception e) {
            // Log error, maybe set an error message for the JSP
            e.printStackTrace(); // Basic error logging
            request.setAttribute("erreurMessage", "Erreur lors de la récupération de vos cours.");
        }

        request.setAttribute("coursList", coursInscrits);
        request.getRequestDispatcher("/jsp/etudiant-accueil.jsp").forward(request, response);
    }
}