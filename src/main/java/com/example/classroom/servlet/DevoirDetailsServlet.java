package com.example.classroom.servlet;

import com.example.classroom.dao.DevoirDAO;
import com.example.classroom.dao.RenduDAO;
import com.example.classroom.model.Devoir;
import com.example.classroom.model.Rendu;
import com.example.classroom.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/devoir/details")
public class DevoirDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int devoirId = Integer.parseInt(request.getParameter("id"));
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            DevoirDAO devoirDAO = new DevoirDAO(em);
            RenduDAO renduDAO = new RenduDAO(em);

            // Récupérer le devoir à partir de la base de données
            Devoir devoir = em.find(Devoir.class, devoirId);
            List<Rendu> rendus = renduDAO.getRendusParDevoir(devoirId);

            // Message de succès
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("successMessage") != null) {
                request.setAttribute("successMessage", session.getAttribute("successMessage"));
                session.removeAttribute("successMessage");
            }

            // Passer les données à la page JSP
            request.setAttribute("devoir", devoir);
            request.setAttribute("rendus", rendus);

            // Forwarder vers la page JSP
            request.getRequestDispatcher("/jsp/devoir-details.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }
}
