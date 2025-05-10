<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, com.example.classroom.dao.CoursDAO, com.example.classroom.model.Cours, com.example.classroom.util.JPAUtil, jakarta.persistence.EntityManager" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Accueil Prof - Classroom</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/prof-accueil.css" />
    <style>
        .btn-retour { background: #00bfff; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; }
        .btn-deconnexion { background: #e74c3c; color: white; border: none; padding: 13px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; float: right; height:  }
    </style>
</head>
<body>
<div class="container">
    <button class="btn-deconnexion" onclick="window.location.href='${pageContext.request.contextPath}/jsp/login.jsp'">Déconnexion</button>
    <header>
        <h1>Bienvenue Professeur 👨‍🏫</h1>
        <button class="add-course-btn">+ Nouveau Cours</button>
    </header>

    <section class="course-list">
        <h2>Vos Cours</h2>

        <%
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            List<Cours> coursList = new ArrayList<>();
            try {
                CoursDAO dao = new CoursDAO(em);
                coursList = dao.getTousLesCours();
            } finally {
                em.close();
            }

            for (Cours cours : coursList) {
        %>
        <div class="course-card">
            <div>
                <h3><%= cours.getNom() %></h3>
                <p>Code Classe : <span class="class-code"><%= cours.getCode() %></span></p>
            </div>
            <!-- Modifier cette ligne -->
            <a href="${pageContext.request.contextPath}/devoir?coursId=<%= cours.getId() %>" class="access-homework-btn">Voir les devoirs</a>
        </div>
        <% } %>
    </section>

    <!-- Formulaire modal -->
    <div id="courseModal" class="course-form-container">
        <h2>Ajouter un nouveau cours</h2>
        <form action="${pageContext.request.contextPath}/ajouter-cours" method="post">
            <input type="text" name="nom" placeholder="Nom du cours" required />
            <textarea name="description" placeholder="Description du cours" required></textarea>
            <button type="submit">Ajouter le cours</button>
        </form>
    </div>
</div>

<script>
    document.querySelector(".add-course-btn").addEventListener("click", () => {
        const modal = document.getElementById("courseModal");
        modal.style.display = modal.style.display === "block" ? "none" : "block";
    });
</script>
</body>
</html>
