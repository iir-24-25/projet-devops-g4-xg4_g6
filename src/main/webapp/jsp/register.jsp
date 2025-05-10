<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Inscription - Classroom</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css" />
</head>

<body>
<div class="register-container">
    <h2>Créer un compte</h2>
    <p class="welcome-text">Inscrivez-vous pour rejoindre la plateforme</p>
    
    <c:if test="${not empty erreur}">
        <div class="error-message">${erreur}</div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/register" method="post">
        <label for="nom">Nom</label>
        <input type="text" id="nom" name="nom" required />

        <label for="prenom">Prénom</label>
        <input type="text" id="prenom" name="prenom" required />

        <label for="email">Email</label>
        <input type="email" id="email" name="email" required />

        <label for="password">Mot de passe</label>
        <input type="password" id="password" name="password" required />

        <label for="telephone">Téléphone</label>
        <input type="tel" id="telephone" name="telephone" required />

        <!-- ✅ Type de compte -->
        <div class="role-selection">
            <p>Type de compte :</p>
            <div class="radio-group">
                <label>
                    <input type="radio" name="role" value="etudiant" required />
                    Étudiant
                </label>
                <label>
                    <input type="radio" name="role" value="professeur" required />
                    Professeur
                </label>
            </div>
        </div>

        <button type="submit">S'inscrire</button>

        <p class="login-link">
            Vous avez déjà un compte ?
            <a href="${pageContext.request.contextPath}/jsp/login.jsp">Se connecter</a>
        </p>
    </form>
</div>
</body>
</html>
