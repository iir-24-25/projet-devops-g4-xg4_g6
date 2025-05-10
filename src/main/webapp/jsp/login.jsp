<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Connexion - Classroom</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css" />
</head>

<body>
<div class="login-container">
    <h2>Connexion</h2>
    
    <c:if test="${not empty erreur}">
        <div class="error-message">${erreur}</div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/login" method="post">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required />

        <label for="password">Mot de passe</label>
        <input type="password" id="password" name="password" required />

        <button type="submit">Se connecter</button>

        <p class="register-link">
            Vous n'avez pas de compte ?
            <a href="${pageContext.request.contextPath}/jsp/register.jsp">Créer un compte</a>
        </p>
    </form>
</div>
</body>
</html>
