# PMT - Project Management Tool

Projet réalisé dans le cadre du bloc de compétences "Intégration, industrialisation et déploiement de logiciel" pour le titre RNCP Niveau 7 - Expert en Ingénierie du Logiciel (ESIEA INTECH).

---

## Présentation

PMT est une application web de gestion de projet collaboratif. Elle permet à des équipes de créer des projets, d'organiser des tâches, d'assigner des responsabilités et de suivre l'avancement du travail.

L'application est composée d'un frontend Angular et d'un backend Spring Boot, avec une base de données MySQL. L'ensemble est conteneurisé avec Docker et déployé via une pipeline CI/CD GitHub Actions.

---

## Technologies utilisées

**Backend :**
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- MySQL 8.0
- Maven

**Frontend :**
- Angular 17
- TypeScript
- Karma / Jasmine (tests)

**DevOps :**
- Docker et Docker Compose
- GitHub Actions
- Docker Hub

---

## Prérequis

Pour lancer le projet avec Docker (méthode recommandée) :
- Docker Desktop (version 20.10 ou supérieure)
- Docker Compose

Pour un déploiement manuel :
- Java JDK 17
- Maven 3.9+
- Node.js 20+
- MySQL 8.0

---

## Procédure de déploiement

### Avec Docker Compose (recommandé)

C'est la méthode la plus simple pour lancer l'application complète.

1. Cloner le repository :

```bash
git clone https://github.com/Mateodiet/pmt.git
cd pmt
```

2. Lancer les conteneurs :

```bash
cd backend
docker-compose up -d
```

3. Vérifier que tout fonctionne :

```bash
docker-compose ps
```

Les trois conteneurs (database, backend, frontend) doivent être en état "running".

4. Accéder à l'application :

- Frontend : http://localhost
- API Backend : http://localhost:8081/api
- Documentation Swagger : http://localhost:8081/swagger-ui.html

5. Pour arrêter l'application :

```bash
docker-compose down
```

### Déploiement manuel

Si vous préférez lancer les services séparément :

**Base de données :**

Créer une base MySQL nommée `pmt_db` et configurer les accès dans `application.properties`.

**Backend :**

```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

Le backend sera accessible sur le port 8081.

**Frontend :**

```bash
cd frontend
npm install --legacy-peer-deps
npm start
```

Le frontend sera accessible sur le port 4200.

### Utiliser les images Docker Hub

Les images sont publiées automatiquement sur Docker Hub à chaque push sur la branche principale.

```bash
docker pull mateodiet/pmt-backend:latest
docker pull mateodiet/pmt-frontend:latest
```

---

## Lancer les tests

**Backend :**

```bash
cd backend
mvn clean test
```

Le rapport de couverture JaCoCo est généré dans `target/coverage-reports/jacoco/index.html`.

**Frontend :**

```bash
cd frontend
npm run test -- --no-watch --code-coverage
```

Le rapport de couverture est généré dans le dossier `coverage/`.

---

## Couverture de code

### Résultats

| Partie | Couverture |
|--------|------------|
| Backend | environ 70% |
| Frontend (services) | 100% |
| Frontend (global) | environ 22% |

### Remarque sur la couverture frontend

La couverture globale du frontend est en dessous des 60% attendus. Cela s'explique par l'architecture d'Angular : les composants sont principalement constitués de templates HTML déclaratifs, qui ne contiennent pas de logique testable unitairement.

En revanche, les services Angular (qui contiennent toute la logique métier : appels API, gestion des données) sont couverts à 100%. C'est là que se trouve le code critique de l'application.

Pour une couverture plus complète de l'interface utilisateur, il faudrait mettre en place des tests end-to-end (avec Cypress ou Playwright par exemple), ce qui dépassait le cadre de ce projet.

---

## Pipeline CI/CD

La pipeline GitHub Actions est déclenchée à chaque push sur les branches `main` ou `master`. Elle exécute les étapes suivantes :

1. **Build et tests backend** : compilation Maven, exécution des tests, génération du rapport JaCoCo
2. **Build et tests frontend** : installation des dépendances, exécution des tests, génération du rapport de couverture
3. **Build et push Docker** : construction des images Docker et publication sur Docker Hub

Les rapports de couverture sont disponibles en téléchargement dans les artifacts de chaque exécution du workflow.

---

## Structure du projet

```
pmt/
├── .github/workflows/ci-cd.yml    # Pipeline CI/CD
├── backend/
│   ├── src/main/java/...          # Code source Java
│   ├── src/main/resources/
│   │   ├── schema.sql             # Structure de la BDD
│   │   └── data.sql               # Données de test
│   ├── src/test/...               # Tests unitaires
│   ├── docs/DATABASE_SCHEMA.md    # Schéma de la BDD
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
└── frontend/
    ├── src/app/...                # Code source Angular
    ├── Dockerfile
    └── package.json
```

---

## Fonctionnalités implémentées

L'application couvre les 12 user stories définies dans l'énoncé :

1. Inscription d'un utilisateur (nom, email, mot de passe)
2. Connexion avec email et mot de passe
3. Création de projet (nom, description, date de début)
4. Invitation de membres par email
5. Attribution de rôles (administrateur, membre, observateur)
6. Création de tâches avec priorité et date d'échéance
7. Assignation de tâches aux membres
8. Mise à jour des tâches
9. Visualisation d'une tâche
10. Tableau de bord avec suivi par statut
11. Notifications par email lors des assignations
12. Historique des modifications des tâches

Note : les notifications email sont implémentées en mode simulation (logs) car la configuration d'un serveur SMTP n'était pas requise pour ce projet.

---

## Documentation de l'API

Une fois le backend lancé, la documentation Swagger est accessible à l'adresse :

http://localhost:8081/swagger-ui.html

Les principaux endpoints sont :

- `/api/user` : gestion des utilisateurs (inscription, connexion, etc.)
- `/api/project` : gestion des projets et des membres
- `/api/tasks` : gestion des tâches et des assignations
- `/api/dashboard` : statistiques pour le tableau de bord

---

## Livrables

Conformément à l'énoncé, voici les livrables du projet :

| Livrable | Localisation |
|----------|--------------|
| Schéma de la base de données | `backend/docs/DATABASE_SCHEMA.md` |
| Script SQL (structure) | `backend/src/main/resources/schema.sql` |
| Script SQL (données de test) | `backend/src/main/resources/data.sql` |
| Repository GitHub | https://github.com/Mateodiet/pmt |
| Rapport de couverture backend | Artifact GitHub Actions |
| Rapport de couverture frontend | Artifact GitHub Actions |
| Dockerfile backend | `backend/Dockerfile` |
| Dockerfile frontend | `frontend/Dockerfile` |
| Pipeline CI/CD | `.github/workflows/ci-cd.yml` |
| Procédure de déploiement | Ce fichier README |

---

## Auteur

Mateo Diet

Projet réalisé en décembre 2024.
