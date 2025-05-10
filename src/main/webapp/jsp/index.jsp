<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - Classroom</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css" />
</head>
<body>
<div class="landing-container">
    <div class="content">
        <h1>Bienvenue sur <span>MyClassroom</span></h1>
        <p>Une plateforme simple pour gérer vos cours, devoirs et interactions entre professeurs et étudiants.</p>

        <div class="buttons">
            <a href="${pageContext.request.contextPath}/jsp/login.jsp" class="btn">Se connecter</a>
            <a href="${pageContext.request.contextPath}/jsp/register.jsp" class="btn btn-secondary">Créer un compte</a>
        </div>
    </div>
</div>
</body>
</html>
