-- ============================================
-- PMT - Project Management Tool
-- Données de test
-- ============================================

-- ============================================
-- UTILISATEURS DE TEST
-- ============================================
INSERT INTO user_tl (user_id, name, email, password, role, contact_number, created_at, is_active) VALUES
(1, 'Alice Martin', 'alice@codesolutions.com', 'password123', 'ADMIN', '0601020304', '2024-01-15', TRUE),
(2, 'Bob Dupont', 'bob@codesolutions.com', 'password123', 'USER', '0605060708', '2024-01-16', TRUE),
(3, 'Claire Bernard', 'claire@codesolutions.com', 'password123', 'USER', '0609101112', '2024-01-17', TRUE),
(4, 'David Leroy', 'david@codesolutions.com', 'password123', 'USER', '0613141516', '2024-02-01', TRUE),
(5, 'Emma Petit', 'emma@codesolutions.com', 'password123', 'USER', '0617181920', '2024-02-10', FALSE);

-- ============================================
-- PROJETS DE TEST
-- ============================================
INSERT INTO project_tl (project_id, project_name, project_description, project_created_date, project_status_updated_date, project_status, task_created_by) VALUES
(1, 'PMT-Backend', 'Développement du backend Spring Boot pour l''application PMT', '2024-01-20', '2024-03-01', 'IN_PROGRESS', 1),
(2, 'PMT-Frontend', 'Développement du frontend Angular pour l''application PMT', '2024-01-22', '2024-02-15', 'IN_PROGRESS', 1),
(3, 'PMT-DevOps', 'Mise en place de la CI/CD et de l''infrastructure Docker', '2024-02-01', NULL, 'TODO', 2),
(4, 'Site Vitrine', 'Création du site vitrine de Code Solutions', '2024-03-01', '2024-03-10', 'DONE', 3);

-- ============================================
-- TÂCHES DE TEST
-- ============================================
INSERT INTO project_task_tl (task_id, task_name, task_description, task_status, task_created_at, project_id_fk, task_created_by) VALUES
-- Tâches pour PMT-Backend
(1, 'Créer les entités JPA', 'Définir les entités User, Project, Task avec leurs annotations JPA', 'DONE', '2024-01-21', 1, 1),
(2, 'Implémenter les repositories', 'Créer les interfaces Repository pour chaque entité', 'DONE', '2024-01-22', 1, 1),
(3, 'Développer les services', 'Implémenter la logique métier dans les classes Service', 'IN_PROGRESS', '2024-01-25', 1, 2),
(4, 'Créer les contrôleurs REST', 'Exposer les endpoints API REST', 'IN_PROGRESS', '2024-01-28', 1, 2),
(5, 'Écrire les tests unitaires', 'Atteindre 60% de couverture de code', 'TODO', '2024-02-01', 1, 1),

-- Tâches pour PMT-Frontend
(6, 'Créer les composants Angular', 'Développer les composants principaux de l''interface', 'DONE', '2024-01-23', 2, 1),
(7, 'Implémenter les services HTTP', 'Créer les services pour communiquer avec le backend', 'DONE', '2024-01-26', 2, 3),
(8, 'Développer le tableau de bord', 'Interface de visualisation des tâches par statut', 'IN_PROGRESS', '2024-02-01', 2, 3),
(9, 'Ajouter les tests Jasmine', 'Écrire les tests unitaires frontend', 'TODO', '2024-02-05', 2, 1),

-- Tâches pour PMT-DevOps
(10, 'Créer les Dockerfiles', 'Dockerfile pour le backend et le frontend', 'TODO', '2024-02-02', 3, 2),
(11, 'Configurer GitHub Actions', 'Pipeline CI/CD pour build, test et deploy', 'TODO', '2024-02-03', 3, 2),
(12, 'Push sur Docker Hub', 'Automatiser le push des images Docker', 'TODO', '2024-02-04', 3, 2),

-- Tâches pour Site Vitrine
(13, 'Design des maquettes', 'Créer les maquettes Figma du site', 'DONE', '2024-03-02', 4, 3),
(14, 'Intégration HTML/CSS', 'Développer les pages statiques', 'DONE', '2024-03-05', 4, 4),
(15, 'Mise en production', 'Déployer le site sur le serveur de production', 'DONE', '2024-03-08', 4, 3);

-- ============================================
-- ASSOCIATIONS PROJET-UTILISATEUR (membres des projets)
-- ============================================
INSERT INTO project_user_bridge_tl (proj_user_id, user_id_fk, project_id_fk, acceptance, assignment_date) VALUES
-- PMT-Backend : Alice (admin), Bob (membre), Claire (observateur)
(1, 1, 1, 'a', '2024-01-20'),
(2, 2, 1, 'a', '2024-01-21'),
(3, 3, 1, 'a', '2024-01-22'),

-- PMT-Frontend : Alice (admin), Claire (membre), David (invité en attente)
(4, 1, 2, 'a', '2024-01-22'),
(5, 3, 2, 'a', '2024-01-23'),
(6, 4, 2, 'p', '2024-02-01'),

-- PMT-DevOps : Bob (admin), David (membre)
(7, 2, 3, 'a', '2024-02-01'),
(8, 4, 3, 'a', '2024-02-02'),

-- Site Vitrine : Claire (admin), David (membre)
(9, 3, 4, 'a', '2024-03-01'),
(10, 4, 4, 'a', '2024-03-02');

-- ============================================
-- ASSOCIATIONS TÂCHE-UTILISATEUR (assignations)
-- ============================================
INSERT INTO task_user_bridge_tl (task_user_id, user_id_fk, task_id_fk, assignment_date) VALUES
-- Assignations PMT-Backend
(1, 1, 1, '2024-01-21'),
(2, 1, 2, '2024-01-22'),
(3, 2, 3, '2024-01-25'),
(4, 2, 4, '2024-01-28'),
(5, 1, 5, '2024-02-01'),

-- Assignations PMT-Frontend
(6, 1, 6, '2024-01-23'),
(7, 3, 7, '2024-01-26'),
(8, 3, 8, '2024-02-01'),
(9, 1, 9, '2024-02-05'),

-- Assignations PMT-DevOps
(10, 2, 10, '2024-02-02'),
(11, 2, 11, '2024-02-03'),
(12, 4, 12, '2024-02-04'),

-- Assignations Site Vitrine
(13, 3, 13, '2024-03-02'),
(14, 4, 14, '2024-03-05'),
(15, 3, 15, '2024-03-08');
