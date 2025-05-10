<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Accueil Étudiant - Classroom</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/etudiant-accueil.css" />
    <style>
        .success-message { color: green; margin-bottom: 10px; }
        .info-message { color: blue; margin-bottom: 10px; }
        .error-message-rejoindre { color: red; margin-bottom: 10px; } /* Specific for join errors */
        .btn-retour { background: #00bfff; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; }
        .btn-deconnexion { background: #e74c3c; color: white; border: none; padding: 11px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; float: right; }
    </style>
</head>

<body>
<div class="container">
    <button class="btn-deconnexion" onclick="window.location.href='${pageContext.request.contextPath}/jsp/login.jsp'">Déconnexion</button>
    <header>
        <h1>Bienvenue Étudiant 👨‍🎓</h1>
        <form class="join-class-form" action="${pageContext.request.contextPath}/etudiant/rejoindre-cours" method="post">
            <input type="text" name="codeClasse" placeholder="Code de la classe" required />
            <button type="submit">Rejoindre</button>
        </form>
        <c:if test="${not empty sessionScope.succesMessageRejoindre}">
            <p class="success-message">${sessionScope.succesMessageRejoindre}</p>
            <c:remove var="succesMessageRejoindre" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.infoMessageRejoindre}">
            <p class="info-message">${sessionScope.infoMessageRejoindre}</p>
            <c:remove var="infoMessageRejoindre" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.erreurMessageRejoindre}">
            <p class="error-message-rejoindre">${sessionScope.erreurMessageRejoindre}</p>
            <c:remove var="erreurMessageRejoindre" scope="session"/>
        </c:if>
    </header>

    <section class="class-list">
        <h2>Vos cours</h2>

        <c:if test="${not empty erreurMessage}">
            <p class="error-message">${erreurMessage}</p> <%-- General error from EtudiantAccueilServlet --%>
        </c:if>

        <c:if test="${empty coursList}">
            <p>Vous n'êtes inscrit à aucun cours pour le moment.</p>
        </c:if>

        <c:if test="${not empty coursList}">
            <c:forEach items="${coursList}" var="cours">
                <div class="class-card">
                    <div>
                        <h3>${cours.nom}</h3>
                        <p>Code : ${cours.code}</p>
                        <%-- <p>Professeur : ${cours.professeur.nom} ${cours.professeur.prenom}</p> --%>
                        <%-- Placeholder for professor info, needs Cours to have a Professeur relationship --%>
                    </div>
                    <a href="${pageContext.request.contextPath}/etudiant/classe?coursId=${cours.id}" class="view-btn">Voir la classe</a>
                </div>
            </c:forEach>
        </c:if>
    </section>
</div>
</body>

</html>
