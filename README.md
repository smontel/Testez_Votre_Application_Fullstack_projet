# Testez_Votre_Application_Fullstack_projet
# Yoga App

Application full-stack de gestion de sessions de yoga, composée d'un frontend Angular et d'un backend Spring Boot.

---

## Prérequis

- **Node.js** et **npm**
- **Java 8**
- **Maven**
- **MySQL**
- **Angular CLI**

---

## Installation

### Frontend

Aller à la racine du projet frontend et installer les dépendances :

```bash
cd front
npm install
```

### Backend

Aller à la racine du projet backend et compiler le projet :

```bash
cd back
mvn clean install
```

### Base de données

1. Lancer une instance MySQL.
2. Renseigner l'URL, le nom d'utilisateur et le mot de passe de la base de données dans le fichier `back/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yoga?allowPublicKeyRetrieval=true
spring.datasource.username=<votre_utilisateur>
spring.datasource.password=<votre_mot_de_passe>
```

3. Exécuter le script SQL d'initialisation situé dans `ressources/sql/script.sql` pour créer le schéma et insérer les données de base.

> **Compte administrateur par défaut :**
> - Login : `yoga@studio.com`
> - Mot de passe : `test!1234`

---

## Lancement de l'application

### Frontend

```bash
cd front
npm run start
```

### Backend

```bash
cd back
mvn spring-boot:run
```

---

## Tests

### Tests unitaires — Backend

```bash
cd back
mvn clean test
```

Un rapport de couverture Jacoco est généré automatiquement à l'issue des tests.

### Tests unitaires — Frontend

```bash
cd front
npm run test
```

### Tests E2E — Cypress

Les tests end-to-end nécessitent deux terminaux ouverts simultanément.

**Terminal 1** — Lancer le frontend avec couverture de code :

```bash
cd front
ng run yoga:serve-coverage
```

**Terminal 2** — Lancer Cypress :

```bash
cd front
npx cypress run
```

Le rapport de couverture E2E sera disponible ici :

```
front/coverage/lcov-report/index.html
```

---

## Stack technique

| Couche     | Technologie                        |
|------------|------------------------------------|
| Frontend   | Angular 14, TypeScript             |
| Backend    | Spring Boot 2.6, Java 8, Maven     |
| Base de données | MySQL                         |
| Tests back | JUnit, Spring Security Test, Jacoco|
| Tests front | Jasmine, Karma                   |
| Tests E2E  | Cypress                            |