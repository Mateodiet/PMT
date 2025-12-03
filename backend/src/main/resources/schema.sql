-- ============================================
-- PMT - Project Management Tool
-- Script de création de la base de données
-- ============================================

-- Suppression des tables existantes (ordre inverse des dépendances)
DROP TABLE IF EXISTS task_user_bridge_tl;
DROP TABLE IF EXISTS project_user_bridge_tl;
DROP TABLE IF EXISTS project_task_tl;
DROP TABLE IF EXISTS project_tl;
DROP TABLE IF EXISTS user_tl;

-- ============================================
-- TABLE: user_tl (Utilisateurs)
-- ============================================
CREATE TABLE user_tl (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) COMMENT 'Rôle global: ADMIN, USER',
    contact_number VARCHAR(20),
    created_at DATE,
    is_active BOOLEAN DEFAULT TRUE
);

-- ============================================
-- TABLE: project_tl (Projets)
-- ============================================
CREATE TABLE project_tl (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL UNIQUE,
    project_description TEXT,
    project_created_date DATE,
    project_status_updated_date DATE,
    project_status VARCHAR(50) COMMENT 'Statut: TODO, IN_PROGRESS, DONE',
    task_created_by BIGINT COMMENT 'FK vers user_tl - Créateur du projet',
    CONSTRAINT fk_project_creator FOREIGN KEY (task_created_by) REFERENCES user_tl(user_id) ON DELETE SET NULL
);

-- ============================================
-- TABLE: project_task_tl (Tâches)
-- ============================================
CREATE TABLE project_task_tl (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(255) NOT NULL UNIQUE,
    task_description TEXT,
    task_status VARCHAR(50) COMMENT 'Statut: TODO, IN_PROGRESS, DONE',
    task_created_at DATE,
    project_id_fk BIGINT NOT NULL COMMENT 'FK vers project_tl',
    task_created_by BIGINT COMMENT 'FK vers user_tl - Créateur de la tâche',
    CONSTRAINT fk_task_project FOREIGN KEY (project_id_fk) REFERENCES project_tl(project_id) ON DELETE CASCADE,
    CONSTRAINT fk_task_creator FOREIGN KEY (task_created_by) REFERENCES user_tl(user_id) ON DELETE SET NULL
);

-- ============================================
-- TABLE: project_user_bridge_tl (Association Projet-Utilisateur)
-- Gère les membres d'un projet et leurs rôles
-- ============================================
CREATE TABLE project_user_bridge_tl (
    proj_user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id_fk BIGINT NOT NULL COMMENT 'FK vers user_tl',
    project_id_fk BIGINT NOT NULL COMMENT 'FK vers project_tl',
    acceptance CHAR(1) DEFAULT 'p' COMMENT 'a=accepted, p=pending',
    assignment_date DATE,
    CONSTRAINT fk_bridge_user FOREIGN KEY (user_id_fk) REFERENCES user_tl(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_bridge_project FOREIGN KEY (project_id_fk) REFERENCES project_tl(project_id) ON DELETE CASCADE,
    CONSTRAINT uk_user_project UNIQUE (user_id_fk, project_id_fk)
);

-- ============================================
-- TABLE: task_user_bridge_tl (Association Tâche-Utilisateur)
-- Gère l'assignation des tâches aux utilisateurs
-- ============================================
CREATE TABLE task_user_bridge_tl (
    task_user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id_fk BIGINT NOT NULL COMMENT 'FK vers user_tl',
    task_id_fk BIGINT NOT NULL COMMENT 'FK vers project_task_tl',
    assignment_date DATE,
    CONSTRAINT fk_taskbridge_user FOREIGN KEY (user_id_fk) REFERENCES user_tl(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_taskbridge_task FOREIGN KEY (task_id_fk) REFERENCES project_task_tl(task_id) ON DELETE CASCADE,
    CONSTRAINT uk_user_task UNIQUE (user_id_fk, task_id_fk)
);

-- ============================================
-- INDEX pour améliorer les performances
-- ============================================
CREATE INDEX idx_user_email ON user_tl(email);
CREATE INDEX idx_project_name ON project_tl(project_name);
CREATE INDEX idx_task_project ON project_task_tl(project_id_fk);
CREATE INDEX idx_task_status ON project_task_tl(task_status);
CREATE INDEX idx_bridge_project ON project_user_bridge_tl(project_id_fk);
CREATE INDEX idx_bridge_user ON project_user_bridge_tl(user_id_fk);
