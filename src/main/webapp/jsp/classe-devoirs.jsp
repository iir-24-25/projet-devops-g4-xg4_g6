<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Gestion des Devoirs - ${cours.nom}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/classe-devoirs.css" />
    <style>
        .btn-retour { background: #00bfff; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; }
        .btn-deconnexion { background: #e74c3c; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; float: right; }
        .btn-details { background: #0074d9; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; text-decoration: none; display: inline-block; }
    </style>
</head>
<body>
<div class="container">
    <button class="btn-deconnexion" onclick="window.location.href='${pageContext.request.contextPath}/jsp/login.jsp'">Déconnexion</button>
    <button onclick="window.history.back();" class="btn-retour">&larr; Retour</button>
    <header>
        <h1>Classe : ${cours.nom}</h1>
    </header>

    <section class="create-homework">
        <h2>Créer un devoir</h2>
        <form action="${pageContext.request.contextPath}/devoir" method="post" enctype="multipart/form-data">
            <input type="hidden" name="coursId" value="${cours.id}" required />
            <input type="text" name="titre" placeholder="Nom du devoir ou TP" required />
            <input type="date" name="date_limite" required />
            <input type="file" name="fichier" accept=".pdf,.doc,.docx" />
            <textarea name="description" placeholder="Texte ou consigne (facultatif)"></textarea>
            <button type="submit">Créer le devoir</button>
        </form>
    </section>

    <section class="homework-list">
        <h2>Devoirs existants</h2>
        <c:if test="${empty devoirs or devoirs.size() == 0}">
            <p>Aucun devoir pour l'instant.</p>
        </c:if>
        <c:if test="${not empty devoirs}">
            <c:forEach items="${devoirs}" var="devoir">
                <div class="homework-card">
                    <div>
                        <h3>${devoir.titre}</h3>
                        <p>Deadline : <fmt:formatDate value="${devoir.dateLimite}" pattern="dd/MM/yyyy"/></p>
                        <c:if test="${not empty devoir.description}">
                            <p class="description">${devoir.description}</p>
                        </c:if>
                        <c:if test="${not empty devoir.fichierNom}">
                            <div class="file-actions">
                                <a href="${pageContext.request.contextPath}/download?devoirId=${devoir.id}" class="file-link" target="_blank">
                                    📥 Télécharger
                                </a>
                                <a href="${pageContext.request.contextPath}/preview?devoirId=${devoir.id}" class="file-link" target="_blank">
                                    👁️ Prévisualiser
                                </a>
                            </div>
                        </c:if>
                    </div>
                    <div class="homework-actions">
                        <a href="${pageContext.request.contextPath}/devoir/details?id=${devoir.id}" class="btn-details">Détails</a>
                        <button class="delete-btn" onclick="deleteDevoir(${devoir.id})">Supprimer</button>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </section>
</div>

<script>
    function deleteDevoir(id) {
        if (confirm('Êtes-vous sûr de vouloir supprimer ce devoir ?')) {
            window.location.href = '${pageContext.request.contextPath}/devoir/delete?id=' + id;
        }
    }
</script>
</body>
</html>