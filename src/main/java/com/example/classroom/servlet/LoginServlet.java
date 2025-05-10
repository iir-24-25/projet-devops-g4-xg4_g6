package com.example.classroom.servlet;

import com.example.classroom.model.Etudiant;
import com.example.classroom.model.Professeur;
import com.example.classroom.util.PasswordUtil; // Import PasswordUtil
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
// No longer need List as we expect single result or NoResultException
// import java.util.List; 

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @PersistenceUnit(unitName = "classroomPU")
    private EntityManagerFactory emf;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String plainPassword = request.getParameter("password"); // Get plain text password

        if (email == null || email.trim().isEmpty() ||
            plainPassword == null || plainPassword.trim().isEmpty()) {
            request.setAttribute("erreur", "Tous les champs sont requis.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }

        EntityManager em = emf.createEntityManager();

        try {
            // Try to log in as Etudiant
            TypedQuery<Etudiant> etuQuery = em.createQuery(
                    "SELECT e FROM Etudiant e WHERE e.email = :email", Etudiant.class);
            etuQuery.setParameter("email", email);
            Etudiant etudiant = null;
            try {
                etudiant = etuQuery.getSingleResult();
            } catch (NoResultException e) {
                // Etudiant not found with this email, continue to check Professeur
            }

            if (etudiant != null) {
                if (PasswordUtil.verifyPassword(plainPassword, etudiant.getPassword(), etudiant.getPasswordSalt())) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", etudiant);
                    session.setAttribute("role", "etudiant");
                    // Redirect to student accueil servlet instead of directly to JSP
                    response.sendRedirect(request.getContextPath() + "/etudiant/accueil");
                    return;
                }
            }

            // Try to log in as Professeur
            TypedQuery<Professeur> profQuery = em.createQuery(
                    "SELECT p FROM Professeur p WHERE p.email = :email", Professeur.class);
            profQuery.setParameter("email", email);
            Professeur professeur = null;
            try {
                professeur = profQuery.getSingleResult();
            } catch (NoResultException e) {
                // Professeur not found with this email
            }

            if (professeur != null) {
                if (PasswordUtil.verifyPassword(plainPassword, professeur.getPassword(), professeur.getPasswordSalt())) {
                    HttpSession session = request.getSession();
                    session.setAttribute("user", professeur);
                    session.setAttribute("role", "professeur");
                    response.sendRedirect(request.getContextPath() + "/jsp/prof-accueil.jsp");
                    return;
                }
            }

            // If neither user type matched or password verification failed
            request.setAttribute("erreur", "Email ou mot de passe incorrect.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            e.printStackTrace(); // Log the exception
            request.setAttribute("erreur", "Une erreur est survenue lors de la connexion.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}