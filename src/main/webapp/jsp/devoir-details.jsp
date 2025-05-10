<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Détails du Devoir</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/devoir-details.css" />
    <style>
        .btn-retour { background: #00bfff; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; }
        .btn-deconnexion { background: #e74c3c; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; margin-bottom: 16px; float: right; }
    </style>
</head>

<body>
<div class="container">
    <button class="btn-deconnexion" onclick="window.location.href='${pageContext.request.contextPath}/jsp/login.jsp'">Déconnexion</button>
    <button onclick="window.history.back();" class="btn-retour">&larr; Retour</button>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>
    <h1>Devoir : ${devoir.titre}</h1>
    <p class="deadline">Deadline :
        <fmt:formatDate value="${devoir.dateLimite}" pattern="dd/MM/yyyy" />
    </p>

    <div class="homework-file">
        <h2>Document du devoir</h2>
        <c:if test="${not empty devoir.fichierNom}">
            <a href="${pageContext.request.contextPath}/download?filename=${devoir.fichierNom}" class="file-link" target="_blank">
                📄 Voir le document
            </a>
        </c:if>
        <c:if test="${empty devoir.fichierNom}">
            <p>Aucun fichier joint.</p>
        </c:if>
    </div>

    <section class="submissions">
        <h2>Rendus des étudiants</h2>

        <c:if test="${empty rendus}">
            <p>Aucun rendu pour ce devoir.</p>
        </c:if>

        <c:forEach var="rendu" items="${rendus}">
            <div class="submission-card">
                <div class="student-info">
                    <h3>${rendu.etudiant.prenom} ${rendu.etudiant.nom}</h3>
                    <a href="${pageContext.request.contextPath}/download?renduId=${rendu.id}" class="file-link" target="_blank">Voir le fichier</a>
                </div>

                <div class="evaluation">
                    <form action="${pageContext.request.contextPath}/evaluer-devoir" method="post">
                        <input type="hidden" name="renduId" value="${rendu.id}" />

                        <div class="form-group">
                            <label for="note">Note /20</label>
                            <input type="number" id="note" name="note" min="0" max="20"
                                   value="${rendu.note}" placeholder="Note /20" required />
                        </div>

                        <div class="form-group">
                            <label for="commentaire">Commentaire</label>
                            <textarea id="commentaire" name="commentaire" placeholder="Commentaire..." required>${rendu.commentaire}</textarea>
                        </div>

                        <button class="submit-feedback" type="submit">Valider</button>
                    </form>
                </div>
            </div>
        </c:forEach>
    </section>
</div>
</body>
<script>
    setTimeout(function() {
        var alert = document.querySelector('.alert-success');
        if(alert) alert.style.display = 'none';
    }, 3000);
</script>
</html>
