# AllinConnect - Mise en Relation Locale de Professionnels

AllinConnect est une plateforme web permettant aux clients de trouver des artisans et professionnels à proximité de chez eux.

## Fonctionnalités

- **Inscription et Connexion** : Les utilisateurs peuvent s'inscrire en tant que Client ou Professionnel.
- **Profil Utilisateur** : Gestion des informations personnelles (nom, prénom, adresse, ville, date de naissance).
- **Recherche de Professionnels** : Les clients peuvent rechercher des professionnels par ville.
- **Système d'Abonnement** : Les utilisateurs disposent d'un type d'abonnement (FREE, BASIC, PREMIUM).
- **Indicateur de Connexion** : Suivi de la première connexion pour une expérience personnalisée.
- **Professionnels** : Support des champs spécifiques comme la profession pour les comptes professionnels.

## Technologies Utilisées

- **Backend** : Spring Boot 4.0.1
- **Langage** : Java 17
- **Base de données** : MySQL 8.x
- **Sécurité** : Spring Security 6 & JWT (JSON Web Token)
- **Persistence** : Spring Data JPA / Hibernate

## Configuration et Installation

1. **Base de données** :
   - Assurez-vous d'avoir un serveur MySQL en cours d'exécution.
   - Créez une base de données nommée `allinconnect`.
   - Modifiez `src/main/resources/application.properties` avec vos identifiants si nécessaire.

2. **Compilation** :
   ```bash
   ./mvnw clean install
   ```

3. **Lancement** :
   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints

### Authentification
- `POST /api/v1/auth/register` : Inscription d'un nouvel utilisateur (Client ou Professionnel).
- `POST /api/v1/auth/authenticate` : Connexion et récupération du token JWT.

### Utilisateurs
- `GET /api/v1/users/professionals?city={city}` : Rechercher des professionnels dans une ville spécifique.

## Structure du Projet

- `com.allinconnect.allinconnectback2.controller` : Contrôleurs REST.
- `com.allinconnect.allinconnectback2.service` : Logique métier.
- `com.allinconnect.allinconnectback2.entity` : Entités JPA.
- `com.allinconnect.allinconnectback2.dto` : Objets de transfert de données pour les requêtes/réponses.
- `com.allinconnect.allinconnectback2.repository` : Interfaces Spring Data JPA.
- `com.allinconnect.allinconnectback2.security` : Configuration de la sécurité JWT.
- `com.allinconnect.allinconnectback2.model` : Énumérations et modèles communs (UserType, SubscriptionType).
# allinconnect-back
