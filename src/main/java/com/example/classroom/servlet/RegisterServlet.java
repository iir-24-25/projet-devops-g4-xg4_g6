package com.example.classroom.servlet;

import com.example.classroom.dao.EtudiantDAO;
import com.example.classroom.dao.ProfesseurDAO;
import com.example.classroom.model.Etudiant;
import com.example.classroom.model.Professeur;
import com.example.classroom.util.PasswordUtil; // Import PasswordUtil

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lecture des paramètres
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String plainPassword = request.getParameter("password"); // Get plain text password
        String telephone = request.getParameter("telephone");
        String role = request.getParameter("role");

        // Vérification simple des champs
        if (nom == null || nom.trim().isEmpty() ||
            prenom == null || prenom.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            plainPassword == null || plainPassword.trim().isEmpty() || // Check plainPassword
            role == null) {
            request.setAttribute("erreur", "Tous les champs sont requis.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        try {
            byte[] salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(plainPassword, salt);

            // Traitement selon le rôle
            if ("etudiant".equalsIgnoreCase(role)) {
                Etudiant etudiant = new Etudiant(nom, prenom, email, telephone);
                etudiant.setPassword(hashedPassword);
                etudiant.setPasswordSalt(salt);
                EtudiantDAO dao = new EtudiantDAO();
                dao.create(etudiant);
            } else if ("professeur".equalsIgnoreCase(role)) {
                Professeur prof = new Professeur(nom, prenom, email, telephone);
                prof.setPassword(hashedPassword);
                prof.setPasswordSalt(salt);
                ProfesseurDAO dao = new ProfesseurDAO();
                dao.create(prof);
            } else {
                request.setAttribute("erreur", "Rôle invalide.");
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                return;
            }

            // Redirection après succès
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?registration=success"); // Optional: add a success param

        } catch (Exception e) {
            // En cas d'erreur (par exemple, email déjà existant from DB constraint)
            request.setAttribute("erreur", "Une erreur est survenue lors de l'inscription : " + e.getMessage());
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
    }
}
