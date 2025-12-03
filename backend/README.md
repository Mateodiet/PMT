# 🚀 PMT - Project Management Tool

[![CI/CD Pipeline](https://github.com/avertak/pmt/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/avertak/pmt/actions/workflows/ci-cd.yml)
[![Docker Hub - Backend](https://img.shields.io/docker/v/avertak/pmt-backend?label=backend&logo=docker)](https://hub.docker.com/r/avertak/pmt-backend)
[![Docker Hub - Frontend](https://img.shields.io/docker/v/avertak/pmt-frontend?label=frontend&logo=docker)](https://hub.docker.com/r/avertak/pmt-frontend)

Application de gestion de projets collaboratifs destinée aux équipes de développement logiciel.

## 📋 Table des matières

- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Prérequis](#-prérequis)
- [Installation rapide avec Docker](#-installation-rapide-avec-docker)
- [Installation manuelle (développement)](#-installation-manuelle-développement)
- [Tests et couverture de code](#-tests-et-couverture-de-code)
- [Pipeline CI/CD](#-pipeline-cicd)
- [API Documentation](#-api-documentation)
- [Structure du projet](#-structure-du-projet)

---

## 🏗 Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│    Frontend     │────▶│    Backend      │────▶│    Database     │
│    (Angular)    │     │  (Spring Boot)  │     │    (MySQL)      │
│    Port: 80     │     │    Port: 8081   │     │   Port: 3306    │
│                 │     │                 │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

## 🛠 Technologies

### Backend
- **Java 17** - Langage de programmation
- **Spring Boot 3.2** - Framework backend
- **Spring Data JPA** - Persistence des données
- **MySQL 8** - Base de données relationnelle
- **H2** - Base de données de test (in-memory)
- **Lombok** - Réduction du boilerplate
- **SpringDoc OpenAPI** - Documentation API (Swagger)
- **JaCoCo** - Couverture de code

### Frontend
- **Angular 17** - Framework frontend
- **TypeScript 5.4** - Langage de programmation
- **Bootstrap 5** - Framework CSS
- **NgBootstrap** - Composants UI
- **Karma/Jasmine** - Tests unitaires

### DevOps
- **Docker** - Conteneurisation
- **Docker Compose** - Orchestration
- **GitHub Actions** - CI/CD
- **Nginx** - Serveur web frontend

---

## 📦 Prérequis

### Pour Docker (recommandé)
- Docker >= 20.0
- Docker Compose >= 2.0

### Pour le développement local
- Java JDK 17
- Maven >= 3.9
- Node.js >= 20
- npm >= 10
- MySQL 8 (ou Docker)

---

## 🐳 Installation rapide avec Docker

### Option 1 : Utiliser les images Docker Hub

```bash
# Télécharger le docker-compose.yml
curl -O https://raw.githubusercontent.com/avertak/pmt/main/backend/docker-compose.yml

# Lancer l'application
docker-compose up -d

# Vérifier que tout fonctionne
docker-compose ps
```

### Option 2 : Builder localement

```bash
# Cloner le repository
git clone https://github.com/avertak/pmt.git
cd pmt/backend

# Lancer avec Docker Compose
docker-compose up -d --build
```

### Accès aux services

| Service | URL |
|---------|-----|
| Frontend | http://localhost |
| Backend API | http://localhost:8081/api |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| Base de données | localhost:3306 |

### Commandes Docker utiles

```bash
# Voir les logs
docker-compose logs -f

# Voir les logs d'un service spécifique
docker-compose logs -f backend

# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (reset complet)
docker-compose down -v

# Reconstruire les images
docker-compose build --no-cache
```

---

## 💻 Installation manuelle (développement)

### 1. Base de données MySQL

```bash
# Avec Docker
docker run -d \
  --name pmt-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=pmt_db \
  -e MYSQL_USER=pmt_user \
  -e MYSQL_PASSWORD=pmt_password \
  -p 3306:3306 \
  mysql:8.0

# Charger le schéma et les données de test
docker exec -i pmt-mysql mysql -upmt_user -ppmt_password pmt_db < backend/src/main/resources/schema.sql
docker exec -i pmt-mysql mysql -upmt_user -ppmt_password pmt_db < backend/src/main/resources/data.sql
```

### 2. Backend Spring Boot

```bash
cd backend

# Installer les dépendances et compiler
mvn clean install -DskipTests

# Lancer l'application
mvn spring-boot:run

# Ou avec le JAR
java -jar target/projectmanagment-0.0.1-SNAPSHOT.jar
```

### 3. Frontend Angular

```bash
cd frontend

# Installer les dépendances
npm ci --legacy-peer-deps

# Lancer en mode développement
npm start

# Build de production
npm run build -- --configuration=production
```

---

## 🧪 Tests et couverture de code

### Backend

```bash
cd backend

# Lancer les tests
mvn test

# Générer le rapport de couverture JaCoCo
mvn test jacoco:report

# Le rapport est disponible dans:
# target/coverage-reports/jacoco/index.html
```

### Frontend

```bash
cd frontend

# Lancer les tests avec couverture
npm run test -- --no-watch --code-coverage

# Le rapport est disponible dans:
# coverage/pmt/index.html
```

### Seuil de couverture requis: **60%**

---

## 🔄 Pipeline CI/CD

La pipeline GitHub Actions exécute automatiquement :

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Build &   │────▶│   Tests &   │────▶│Docker Build │
│   Compile   │     │  Coverage   │     │   & Push    │
└─────────────┘     └─────────────┘     └─────────────┘
```

### Étapes de la pipeline

1. **Backend Build & Test**
   - Compilation Maven
   - Exécution des tests unitaires et d'intégration
   - Génération du rapport JaCoCo
   - Création du JAR

2. **Frontend Build & Test**
   - Installation des dépendances npm
   - Linting du code
   - Exécution des tests Karma
   - Build de production

3. **Docker Build & Push** (sur `main`/`master` uniquement)
   - Build des images Docker multi-stage
   - Push sur Docker Hub
   - Tags: `latest` et SHA du commit

### Configuration requise

Pour que le push Docker Hub fonctionne, configurez le secret suivant dans GitHub :

```
DOCKER_HUB_TOKEN : Votre token d'accès Docker Hub
```

### Images Docker

| Image | Docker Hub |
|-------|------------|
| Backend | `avertak/pmt-backend:latest` |
| Frontend | `avertak/pmt-frontend:latest` |

---

## 📚 API Documentation

L'API est documentée avec Swagger/OpenAPI.

**Accès Swagger UI** : http://localhost:8081/swagger-ui.html

### Endpoints principaux

#### Users (`/api/user`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/signup` | Inscription |
| POST | `/login` | Connexion |
| GET | `/getUserByEmail` | Récupérer un utilisateur |
| GET | `/getAllUsers` | Liste des utilisateurs |
| PUT | `/updateUser/{email}` | Mettre à jour |
| DELETE | `/deleteUser/{email}` | Supprimer |

#### Projects (`/api/project`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/create` | Créer un projet |
| GET | `/getAllProjects` | Liste des projets |
| GET | `/getProject` | Détails d'un projet |
| PUT | `/updateProject/{name}` | Mettre à jour |
| DELETE | `/deleteProject/{name}` | Supprimer |
| GET | `/invite/{email}/{project}` | Inviter un membre |
| GET | `/projectInviteAccept/{email}/{project}` | Accepter invitation |

#### Tasks (`/api/tasks`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/createtask` | Créer une tâche |
| GET | `/getAllTasks` | Liste des tâches |
| GET | `/getTask` | Détails d'une tâche |
| GET | `/getTaskByProject` | Tâches par projet |
| PUT | `/updateTask/{name}` | Mettre à jour |
| DELETE | `/deleteTask/{name}` | Supprimer |
| POST | `/assignTask` | Assigner une tâche |

---

## 📁 Structure du projet

```
pmt/
├── backend/                          # Application Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/.../
│   │   │   │   ├── config/          # Configuration (Swagger, CORS)
│   │   │   │   ├── controller/      # Contrôleurs REST
│   │   │   │   ├── entities/        # Entités JPA
│   │   │   │   ├── models/          # DTOs
│   │   │   │   ├── repositories/    # Repositories Spring Data
│   │   │   │   └── services/        # Logique métier
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── schema.sql       # Script DDL
│   │   │       └── data.sql         # Données de test
│   │   └── test/                    # Tests unitaires et intégration
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
│
├── frontend/                         # Application Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── home/               # Composants principaux
│   │   │   ├── services/           # Services HTTP
│   │   │   ├── shared/             # Guards, interceptors
│   │   │   ├── sign-in/            # Connexion
│   │   │   └── sign-up/            # Inscription
│   │   └── assets/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
└── .github/
    └── workflows/
        └── ci-cd.yml                # Pipeline CI/CD
```

---

## 🔐 Variables d'environnement

### Backend

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:mysql://localhost:3306/pmt_db` |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur BDD | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe BDD | `admin@1234` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Mode DDL | `update` |
| `SPRING_PROFILES_ACTIVE` | Profil Spring | `prod` |

---

## 👥 Auteur

**Étude de cas - Master 2 Expert en Ingénierie du Logiciel**

VISIPLUS / ESIEA INTECH

---

## 📄 Licence

Ce projet est développé dans le cadre d'une étude de cas académique.
