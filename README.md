# AllinConnect - Mise en Relation Locale de Professionnels

AllinConnect est une plateforme web permettant aux clients de trouver des artisans et professionnels à proximité de chez eux.

## Fonctionnalités

- **Inscription et Connexion** : Les utilisateurs peuvent s'inscrire en tant que Client ou Professionnel.
- **Profil Utilisateur** : Gestion des informations personnelles (nom, prénom, adresse, ville, date de naissance) et changement de mot de passe.
- **Recherche de Professionnels** : Recherche par ville et par catégorie professionnelle.
- **Système d'Abonnement** : Les utilisateurs disposent d'un type d'abonnement (FREE, BASIC, PREMIUM). Les professionnels peuvent souscrire à des plans payants.
- **Gestion des Offres** : Les professionnels peuvent créer, mettre à jour, archiver et supprimer des offres de services.
- **Évaluations et Commentaires** : Les clients peuvent évaluer les professionnels avec un score et un commentaire.
- **Favoris** : Les utilisateurs peuvent ajouter des professionnels à leur liste de favoris.
- **Indicateur de Connexion** : Suivi de la première connexion pour une expérience personnalisée.

## Technologies Utilisées

- **Backend** : Spring Boot 4.0.1
- **Langage** : Java 21
- **Base de données** : 
  - **Production** : MySQL 8.x
  - **Tests** : H2 (Base de données en mémoire)
- **Sécurité** : Spring Security 6 & JWT (JSON Web Token)
- **Persistence** : Spring Data JPA / Hibernate
- **Outils** : Lombok

## Configuration et Installation

1. **Base de données** :
   - Assurez-vous d'avoir un serveur MySQL en cours d'exécution pour le développement.
   - Créez une base de données nommée `allinconnect`.
   - Modifiez `src/main/resources/application.properties` avec vos identifiants si nécessaire.
   - *Note : Les tests utilisent automatiquement une base H2 en mémoire.*

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
- `POST /api/v1/auth/register` : Inscription d'un nouvel utilisateur.
- `POST /api/v1/auth/authenticate` : Connexion et récupération du token JWT.
- `POST /api/v1/auth/forgot-password` : Demande de réinitialisation de mot de passe.
- `POST /api/v1/auth/reset-password` : Réinitialisation du mot de passe avec jeton.

### Utilisateurs
- `GET /api/v1/users/me` : Récupérer mon profil.
- `GET /api/v1/users/professionals/search` : Rechercher des professionnels par ville et catégorie.
- `POST /api/v1/users/change-password` : Changer son mot de passe.
- `POST /api/v1/users/favorites/{id}` : Ajouter un professionnel aux favoris.
- `GET /api/v1/users/favorites` : Lister mes favoris.

### Offres
- `GET /api/v1/offers` : Lister toutes les offres (avec filtres optionnels).
- `POST /api/v1/offers` : Créer une nouvelle offre (Professionnel).
- `GET /api/v1/offers/my-offers` : Lister mes propres offres.
- `PUT /api/v1/offers/{id}` : Modifier une offre.
- `DELETE /api/v1/offers/{id}` : Supprimer une offre.

### Évaluations
- `POST /api/v1/ratings` : Laisser une évaluation.
- `GET /api/v1/ratings/user/{userId}` : Voir les évaluations d'un utilisateur.
- `GET /api/v1/ratings/user/{userId}/average` : Voir la note moyenne d'un utilisateur.

### Abonnements
- `GET /api/v1/subscriptions/plans` : Lister les plans d'abonnement disponibles.
- `POST /api/v1/subscriptions/subscribe/{planId}` : Souscrire à un plan.
- `GET /api/v1/subscriptions/my-payments` : Voir mon historique de paiements.

## Structure du Projet

- `com.allinconnect.controller` : Contrôleurs REST.
- `com.allinconnect.service` : Logique métier.
- `com.allinconnect.entity` : Entités JPA.
- `com.allinconnect.dto` : Objets de transfert de données pour les requêtes/réponses.
- `com.allinconnect.repository` : Interfaces Spring Data JPA.
- `com.allinconnect.security` : Configuration de la sécurité JWT.
- `com.allinconnect.model` : Énumérations et modèles communs (UserType, SubscriptionType).
# allinconnect-back
