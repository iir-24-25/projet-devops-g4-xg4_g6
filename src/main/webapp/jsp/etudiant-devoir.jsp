<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Mon Devoir : ${devoir.titre}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/etudiant-devoir.css" />
    <style>
        .btn-retour { background: #00bfff; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; }
        .btn-deconnexion { background: #e74c3c; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; float: right; }
    </style>
</head>

<body>
<div class="container">
    <button class="btn-deconnexion" onclick="window.location.href='${pageContext.request.contextPath}/jsp/login.jsp'">Déconnexion</button>
    <button onclick="window.history.back();" class="btn-retour">&larr; Retour</button>
    <c:if test="${not empty erreurMessage}">
        <p class="error-message">${erreurMessage}</p>
        <p><a href="${pageContext.request.contextPath}/etudiant/accueil">Retour à l'accueil</a></p>
    </c:if>

    <c:if test="${empty erreurMessage and not empty devoir and not empty rendu}">
        <h1>Devoir : ${devoir.titre}</h1>
        <p class="deadline">Date limite : <fmt:formatDate value="${devoir.dateLimite}" pattern="dd/MM/yyyy" /></p>
        <%-- <p><a href="${pageContext.request.contextPath}/etudiant/classe?coursId=${devoir.cours.id}" class="back-btn">← Retour à la classe</a></p> --%>


        <!-- Document original du prof -->
        <div class="section">
            <h2>Document du professeur</h2>
            <c:if test="${not empty devoir.fichierNom}">
                <a href="${pageContext.request.contextPath}/preview?devoirId=${devoir.id}" target="_blank" class="file-link">📄 Voir le fichier du devoir</a>
            </c:if>
            <c:if test="${empty devoir.fichierNom}">
                <p>Aucun document fourni par le professeur pour ce devoir.</p>
            </c:if>
        </div>

        <!-- Document rendu par l'étudiant -->
        <div class="section">
            <h2>Mon rendu</h2>
            <c:if test="${not empty rendu.fichierNom}"> <%-- Check fichierNom or fichierData --%>
                <a href="${pageContext.request.contextPath}/download?renduId=${rendu.id}" target="_blank" class="file-link">📎 Voir mon fichier rendu</a>
                <p>Soumis le : <fmt:formatDate value="${rendu.dateRendu}" pattern="dd/MM/yyyy HH:mm:ss" /></p> <%-- Added seconds to timestamp --%>
            </c:if>
            <c:if test="${empty rendu.fichierNom}">
                <p>Aucun fichier soumis pour ce rendu.</p>
            </c:if>
        </div>

        <!-- Note et commentaire -->
        <div class="section">
            <h2>Évaluation</h2>
            <c:choose>
                <c:when test="${rendu.note != null}">
                    <p class="note">Note : <strong>${rendu.note} / 20</strong></p>
                    <div class="commentaire">
                        <h3>Commentaire du professeur :</h3>
                        <p><c:out value="${rendu.commentaire}" default="Aucun commentaire."/></p>
                    </div>
                </c:when>
                <c:otherwise>
                    <p>Ce devoir n'a pas encore été évalué.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>
</div>
</body>

</html>
