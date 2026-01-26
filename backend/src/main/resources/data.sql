-- ============================================
-- PMT - Données de test
-- ============================================

-- ============================================
-- Utilisateurs
-- ============================================
INSERT INTO user_tl (user_id, name, email, password, role, contact_number, is_active) VALUES
(1, 'Alice Martin', 'alice.martin@pmt.com', 'password123', 'ADMIN', '0601020304', true),
(2, 'Bob Dupont', 'bob.dupont@pmt.com', 'password123', 'USER', '0605060708', true),
(3, 'Claire Bernard', 'claire.bernard@pmt.com', 'password123', 'USER', '0609101112', true),
(4, 'David Leroy', 'david.leroy@pmt.com', 'password123', 'USER', '0613141516', true),
(5, 'Emma Petit', 'emma.petit@pmt.com', 'password123', 'USER', '0617181920', true);
-- ============================================
-- Projets
-- ============================================
INSERT INTO project_tl (project_id, project_name, project_description, project_status, task_created_by) VALUES
(1, 'PMT-Backend', 'Développement de l''API REST Spring Boot', 'ACTIVE', 1),
(2, 'PMT-Frontend', 'Développement de l''interface Angular', 'ACTIVE', 1),
(3, 'PMT-DevOps', 'Infrastructure et déploiement CI/CD', 'ACTIVE', 2),
(4, 'PMT-Documentation', 'Rédaction de la documentation technique', 'COMPLETED', 3);

-- ============================================
-- Tâches (avec priority et dueDate)
-- ============================================
INSERT INTO project_task_tl (task_id, task_name, task_description, task_status, priority, task_due_date, project_id_fk, task_created_by) VALUES
-- Projet Backend
(1, 'API-Authentication', 'Implémenter JWT authentication', 'DONE', 'HIGH', '2025-01-15', 1, 1),
(2, 'API-Users-CRUD', 'Endpoints CRUD pour les utilisateurs', 'DONE', 'HIGH', '2025-01-20', 1, 1),
(3, 'API-Projects-CRUD', 'Endpoints CRUD pour les projets', 'DONE', 'HIGH', '2025-01-25', 1, 1),
(4, 'API-Tasks-CRUD', 'Endpoints CRUD pour les tâches', 'IN_PROGRESS', 'HIGH', '2025-02-01', 1, 2),
(5, 'API-Email-Service', 'Service d''envoi de notifications email', 'TODO', 'MEDIUM', '2025-02-10', 1, 1),
(6, 'API-Task-History', 'Historique des modifications de tâches', 'IN_PROGRESS', 'MEDIUM', '2025-02-15', 1, 2),

-- Projet Frontend
(7, 'UI-Login-Page', 'Page de connexion et inscription', 'DONE', 'HIGH', '2025-01-18', 2, 3),
(8, 'UI-Dashboard', 'Tableau de bord principal', 'DONE', 'HIGH', '2025-01-28', 2, 3),
(9, 'UI-Project-List', 'Liste et gestion des projets', 'IN_PROGRESS', 'MEDIUM', '2025-02-05', 2, 4),
(10, 'UI-Task-Board', 'Kanban board pour les tâches', 'TODO', 'HIGH', '2025-02-20', 2, 3),
(11, 'UI-Notifications', 'Système de notifications', 'TODO', 'LOW', '2025-03-01', 2, 4),

-- Projet DevOps
(12, 'Docker-Backend', 'Dockerfile pour le backend', 'DONE', 'CRITICAL', '2025-01-10', 3, 2),
(13, 'Docker-Frontend', 'Dockerfile pour le frontend', 'DONE', 'CRITICAL', '2025-01-12', 3, 2),
(14, 'CI-Pipeline', 'Pipeline GitHub Actions', 'DONE', 'CRITICAL', '2025-01-20', 3, 2),
(15, 'CD-DockerHub', 'Push automatique sur Docker Hub', 'DONE', 'HIGH', '2025-01-25', 3, 2);

-- ============================================
-- Membres des projets (avec rôles)
-- ============================================
INSERT INTO project_user_bridge_tl (proj_user_id, user_id_fk, project_id_fk, project_role, acceptance) VALUES
-- Projet Backend
(1, 1, 1, 'ADMIN', 'a'),
(2, 2, 1, 'MEMBER', 'a'),
(3, 5, 1, 'OBSERVER', 'a'),

-- Projet Frontend
(4, 1, 2, 'ADMIN', 'a'),
(5, 3, 2, 'MEMBER', 'a'),
(6, 4, 2, 'MEMBER', 'a'),

-- Projet DevOps
(7, 2, 3, 'ADMIN', 'a'),
(8, 1, 3, 'MEMBER', 'a'),
(9, 5, 3, 'OBSERVER', 'p'),

-- Projet Documentation
(10, 3, 4, 'ADMIN', 'a'),
(11, 4, 4, 'MEMBER', 'a');

-- ============================================
-- Assignation des tâches aux utilisateurs
-- ============================================
INSERT INTO task_user_bridge_tl (task_user_id, user_id_fk, task_id_fk) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 2, 3),
(4, 2, 4),
(5, 1, 5),
(6, 2, 6),
(7, 3, 7),
(8, 3, 8),
(9, 4, 9),
(10, 3, 10),
(11, 4, 11),
(12, 2, 12),
(13, 2, 13),
(14, 2, 14),
(15, 2, 15);

ALTER TABLE user_tl AUTO_INCREMENT = 100;
ALTER TABLE project_tl AUTO_INCREMENT = 100;
ALTER TABLE project_task_tl AUTO_INCREMENT = 100;
ALTER TABLE project_user_bridge_tl AUTO_INCREMENT = 100;
ALTER TABLE task_user_bridge_tl AUTO_INCREMENT = 100;
ALTER TABLE task_history_tl AUTO_INCREMENT = 100;