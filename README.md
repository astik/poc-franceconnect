# Workshop FranceConnect 20/01/2016

Cette application très simple est un POC pour l'intégration de FranceConnect en tant que fournisseur de service.
Le code est grossier est nécessite des restructurations pour garantir un minimum de qualité, encore une fois, c'est un POC =)



## Pourquoi ce POC ?

Ce POC permet de valider l'authentification d'un utilisateur à travers l'API FranceConnect.
Dans cet exemple, il n'est pas question de récupérer des informations pour rapprocher des comptes mais uniquement de valider l'authentification déléguée à un fournisseur d'identité.

Ce besoin semble pertinent pour pas mal de solution :

- mise en place dans le SSO CAS
- intégration dans des solutions opensource (Jahia, Liferay, ...)
- intégration dans des applications spécifiques (par exemple, à travers Apache Shiro, Spring Security, ...)



## Pré-requis

Vous aurez besoin pour cette démo d'un compte FranceConnect.

Dans la configuration de votre compte (https://doc.integ01.dev-franceconnect.fr/updateClient), vous devrez positionner ces configurations :

Urls de callback (une par ligne :

	http://localhost:8080/poc-fc/fc/cb

Urls de redirection de déconnexion (une par ligne) :

	http://localhost:8080/poc-fc/logout.jsp



## Initialisation

Pour faire fonctionner l'application :

- cloner le dépôt git 

	git clone

- construire l'application :

	cd poc-fc
	mvn install

- lancer tomcat :

	mvn tomcat:run

- accéder avec votre navigateur à l'adresse :

	http://localhost:8080/poc-fc/



## Cinématique

- cliquer sur le lien Access a secured resource, cette ressource accessible par l'URL /poc-fc/secured/ ; cette sous-arborescence est sécurisée et nécessite une authentification 
- vous devriez être redirigé vers FranceConnect automatiquement
- choisir "impots.gouv.fr" et utiliser les credentials suivant :

	Numéro fiscal : mireille@binks.fr
	Mot de passe : 123

- vous devriez accéder à la ressource protégée



## Kit FranceConnect

Après authentification, le kit (https://doc.integ01.dev-franceconnect.fr/ressources-graphiques) est visible  par l'affichage de l'image FranceConnect en haut à gauche. en cliquant dessus vous accéder à plusieurs outils (statistiques et déconnexion par exemple).
    


## Dernière chose

Ce projet est un POC réalisé pendant le workshop FranceConnect du 20/01/2016 (http://www.meetup.com/fr-FR/FranceConnect/events/227568100/).
Merci à Octo pour leur accueil et l'encadrement.
