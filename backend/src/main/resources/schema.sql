-- ============================================
-- PMT - Project Management Tool
-- Script de création de la base de données
-- ============================================

-- Suppression des tables existantes (ordre inverse des dépendances)
DROP TABLE IF EXISTS task_history_tl;
DROP TABLE IF EXISTS task_user_bridge_tl;
DROP TABLE IF EXISTS project_user_bridge_tl;
DROP TABLE IF EXISTS project_task_tl;
DROP TABLE IF EXISTS project_tl;
DROP TABLE IF EXISTS user_tl;

-- ============================================
-- Table des utilisateurs
-- ============================================
CREATE TABLE user_tl (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    contact_number VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
);

-- ============================================
-- Table des projets (avec date de début)
-- ============================================
CREATE TABLE project_tl (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(150) NOT NULL UNIQUE,
    project_description TEXT,
    project_start_date DATE,
    project_created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_status VARCHAR(50) DEFAULT 'ACTIVE',
    project_status_updated_date DATE,
    task_created_by BIGINT,
    
    INDEX idx_project_name (project_name),
    INDEX idx_project_status (project_status),
    
    CONSTRAINT fk_project_creator 
        FOREIGN KEY (task_created_by) REFERENCES user_tl(user_id)
        ON DELETE SET NULL
);

-- ============================================
-- Table des tâches (avec priority et dueDate)
-- ============================================
CREATE TABLE project_task_tl (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(150) NOT NULL UNIQUE,
    task_description TEXT,
    task_status VARCHAR(50) DEFAULT 'TODO',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    task_due_date DATE,
    task_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    task_completed_at TIMESTAMP,
    project_id_fk BIGINT,
    task_created_by BIGINT,
    
    INDEX idx_task_name (task_name),
    INDEX idx_task_status (task_status),
    INDEX idx_task_priority (priority),
    INDEX idx_task_project (project_id_fk),
    INDEX idx_task_due_date (task_due_date),
    
    CONSTRAINT fk_task_project 
        FOREIGN KEY (project_id_fk) REFERENCES project_tl(project_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_task_creator 
        FOREIGN KEY (task_created_by) REFERENCES user_tl(user_id)
        ON DELETE SET NULL
);

-- ============================================
-- Table de liaison projet-utilisateur (avec rôles)
-- ============================================
CREATE TABLE project_user_bridge_tl (
    proj_user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id_fk BIGINT NOT NULL,
    project_id_fk BIGINT NOT NULL,
    project_role VARCHAR(20) DEFAULT 'MEMBER',
    acceptance CHAR(1) DEFAULT 'p',
    assignment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_bridge_user (user_id_fk),
    INDEX idx_bridge_project (project_id_fk),
    INDEX idx_bridge_role (project_role),
    
    CONSTRAINT fk_bridge_user 
        FOREIGN KEY (user_id_fk) REFERENCES user_tl(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_bridge_project 
        FOREIGN KEY (project_id_fk) REFERENCES project_tl(project_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_user_project UNIQUE (user_id_fk, project_id_fk)
);

-- ============================================
-- Table de liaison tâche-utilisateur
-- ============================================
CREATE TABLE task_user_bridge_tl (
    task_user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id_fk BIGINT NOT NULL,
    task_id_fk BIGINT NOT NULL,
    assignment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_task_bridge_user (user_id_fk),
    INDEX idx_task_bridge_task (task_id_fk),
    
    CONSTRAINT fk_task_bridge_user 
        FOREIGN KEY (user_id_fk) REFERENCES user_tl(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_task_bridge_task 
        FOREIGN KEY (task_id_fk) REFERENCES project_task_tl(task_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_user_task UNIQUE (user_id_fk, task_id_fk)
);

-- ============================================
-- Table d'historique des modifications de tâches
-- ============================================
CREATE TABLE task_history_tl (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id_fk BIGINT NOT NULL,
    modified_by_user_id BIGINT,
    modified_by_email VARCHAR(150),
    field_name VARCHAR(100) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    change_description VARCHAR(500),
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_history_task (task_id_fk),
    INDEX idx_history_user (modified_by_user_id),
    INDEX idx_history_date (modified_at),
    
    CONSTRAINT fk_history_task 
        FOREIGN KEY (task_id_fk) REFERENCES project_task_tl(task_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_history_user 
        FOREIGN KEY (modified_by_user_id) REFERENCES user_tl(user_id)
        ON DELETE SET NULL
);