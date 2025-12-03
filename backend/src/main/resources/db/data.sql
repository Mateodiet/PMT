-- ============================================
-- PMT - Project Management Tool
-- Script SQL d'insertion des données de test
-- ============================================

-- ============================================
-- Utilisateurs de test
-- ============================================
INSERT INTO user_tl (name, email, password, role, contact_number, is_active) VALUES
('John Doe', 'john.doe@codesolutions.com', 'password123', 'ADMIN', '+33612345678', TRUE),
('Jane Smith', 'jane.smith@codesolutions.com', 'password123', 'MEMBER', '+33623456789', TRUE),
('Bob Wilson', 'bob.wilson@codesolutions.com', 'password123', 'MEMBER', '+33634567890', TRUE),
('Alice Martin', 'alice.martin@codesolutions.com', 'password123', 'OBSERVER', '+33645678901', TRUE),
('Charlie Brown', 'charlie.brown@codesolutions.com', 'password123', 'MEMBER', '+33656789012', TRUE);

-- ============================================
-- Projets de test
-- ============================================
INSERT INTO project_tl (project_name, project_description, project_status, task_created_by) VALUES
('PMT Development', 'Développement de l''application Project Management Tool', 'ACTIVE', 1),
('E-Commerce Platform', 'Plateforme e-commerce pour client XYZ', 'ACTIVE', 1),
('Mobile App', 'Application mobile de gestion de tâches', 'PLANNING', 2),
('API Gateway', 'Mise en place d''une passerelle API centralisée', 'COMPLETED', 1);

-- ============================================
-- Tâches de test
-- ============================================
-- Projet PMT Development (project_id = 1)
INSERT INTO project_task_tl (task_name, task_description, task_status, project_id_fk, task_created_by) VALUES
('Setup Backend', 'Configuration initiale du projet Spring Boot', 'DONE', 1, 1),
('Setup Frontend', 'Configuration initiale du projet Angular', 'DONE', 1, 1),
('User Authentication', 'Implémenter l''inscription et la connexion', 'IN_PROGRESS', 1, 2),
('Project CRUD', 'Créer les opérations CRUD pour les projets', 'IN_PROGRESS', 1, 2),
('Task Management', 'Implémenter la gestion des tâches', 'TODO', 1, 1),
('Dashboard', 'Créer le tableau de bord avec les statistiques', 'TODO', 1, 3),
('Docker Setup', 'Dockeriser l''application frontend et backend', 'TODO', 1, 1),
('CI/CD Pipeline', 'Configurer GitHub Actions pour le déploiement', 'TODO', 1, 1);

-- Projet E-Commerce (project_id = 2)
INSERT INTO project_task_tl (task_name, task_description, task_status, project_id_fk, task_created_by) VALUES
('Product Catalog', 'Créer le catalogue de produits', 'IN_PROGRESS', 2, 2),
('Shopping Cart', 'Implémenter le panier d''achat', 'TODO', 2, 3),
('Payment Integration', 'Intégrer Stripe pour les paiements', 'TODO', 2, 1);

-- Projet Mobile App (project_id = 3)
INSERT INTO project_task_tl (task_name, task_description, task_status, project_id_fk, task_created_by) VALUES
('UI Design', 'Concevoir les maquettes de l''application', 'IN_PROGRESS', 3, 4),
('Flutter Setup', 'Initialiser le projet Flutter', 'TODO', 3, 2);

-- ============================================
-- Associations Projet-Utilisateur
-- ============================================
-- PMT Development : John (Admin), Jane (Member), Bob (Member), Alice (Observer)
INSERT INTO project_user_bridge_tl (user_id_fk, project_id_fk, acceptance, user_role) VALUES
(1, 1, 'a', 'ADMIN'),
(2, 1, 'a', 'MEMBER'),
(3, 1, 'a', 'MEMBER'),
(4, 1, 'a', 'OBSERVER');

-- E-Commerce : John (Admin), Jane (Member), Charlie (Member)
INSERT INTO project_user_bridge_tl (user_id_fk, project_id_fk, acceptance, user_role) VALUES
(1, 2, 'a', 'ADMIN'),
(2, 2, 'a', 'MEMBER'),
(5, 2, 'a', 'MEMBER');

-- Mobile App : Jane (Admin), Alice (Observer), Charlie (Member) - pending invitation
INSERT INTO project_user_bridge_tl (user_id_fk, project_id_fk, acceptance, user_role) VALUES
(2, 3, 'a', 'ADMIN'),
(4, 3, 'a', 'OBSERVER'),
(5, 3, 'p', 'MEMBER');

-- ============================================
-- Associations Tâche-Utilisateur (assignations)
-- ============================================
-- PMT Development tasks
INSERT INTO task_user_bridge_tl (user_id_fk, task_id_fk) VALUES
(1, 1),  -- John -> Setup Backend
(2, 2),  -- Jane -> Setup Frontend
(2, 3),  -- Jane -> User Authentication
(3, 4),  -- Bob -> Project CRUD
(3, 5),  -- Bob -> Task Management
(4, 6),  -- Alice -> Dashboard (observer can view)
(1, 7),  -- John -> Docker Setup
(1, 8);  -- John -> CI/CD Pipeline

-- E-Commerce tasks
INSERT INTO task_user_bridge_tl (user_id_fk, task_id_fk) VALUES
(2, 9),   -- Jane -> Product Catalog
(5, 10),  -- Charlie -> Shopping Cart
(1, 11);  -- John -> Payment Integration

-- Mobile App tasks
INSERT INTO task_user_bridge_tl (user_id_fk, task_id_fk) VALUES
(4, 12),  -- Alice -> UI Design
(2, 13);  -- Jane -> Flutter Setup
