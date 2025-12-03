-- ============================================
-- PMT - Project Management Tool
-- Script SQL de création de la base de données
-- ============================================

-- Suppression des tables existantes (ordre inverse des dépendances)
DROP TABLE IF EXISTS task_user_bridge_tl;
DROP TABLE IF EXISTS project_user_bridge_tl;
DROP TABLE IF EXISTS project_task_tl;
DROP TABLE IF EXISTS project_tl;
DROP TABLE IF EXISTS user_tl;

-- ============================================
-- Table: user_tl (Utilisateurs)
-- ============================================
CREATE TABLE user_tl (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'MEMBER',
    contact_number VARCHAR(20),
    created_at DATE DEFAULT (CURRENT_DATE),
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_user_email (email),
    INDEX idx_user_active (is_active)
);

-- ============================================
-- Table: project_tl (Projets)
-- ============================================
CREATE TABLE project_tl (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(150) NOT NULL UNIQUE,
    project_description TEXT,
    project_created_date DATE DEFAULT (CURRENT_DATE),
    project_status_updated_date DATE,
    project_status VARCHAR(50) DEFAULT 'ACTIVE',
    task_created_by BIGINT,
    
    INDEX idx_project_name (project_name),
    INDEX idx_project_status (project_status),
    
    CONSTRAINT fk_project_creator 
        FOREIGN KEY (task_created_by) REFERENCES user_tl(user_id)
        ON DELETE SET NULL
);

-- ============================================
-- Table: project_task_tl (Tâches)
-- ============================================
CREATE TABLE project_task_tl (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(150) NOT NULL UNIQUE,
    task_description TEXT,
    task_status VARCHAR(50) DEFAULT 'TODO',
    task_created_at DATE DEFAULT (CURRENT_DATE),
    project_id_fk BIGINT NOT NULL,
    task_created_by BIGINT,
    
    INDEX idx_task_name (task_name),
    INDEX idx_task_status (task_status),
    INDEX idx_task_project (project_id_fk),
    
    CONSTRAINT fk_task_project 
        FOREIGN KEY (project_id_fk) REFERENCES project_tl(project_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_task_creator 
        FOREIGN KEY (task_created_by) REFERENCES user_tl(user_id)
        ON DELETE SET NULL
);

-- ============================================
-- Table: project_user_bridge_tl (Association Projet-Utilisateur)
-- ============================================
CREATE TABLE project_user_bridge_tl (
    proj_user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id_fk BIGINT NOT NULL,
    project_id_fk BIGINT NOT NULL,
    acceptance CHAR(1) DEFAULT 'p' COMMENT 'a=accepted, p=pending',
    assignment_date DATE DEFAULT (CURRENT_DATE),
    user_role VARCHAR(50) DEFAULT 'MEMBER' COMMENT 'ADMIN, MEMBER, OBSERVER',
    
    INDEX idx_bridge_user (user_id_fk),
    INDEX idx_bridge_project (project_id_fk),
    
    CONSTRAINT fk_bridge_user 
        FOREIGN KEY (user_id_fk) REFERENCES user_tl(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_bridge_project 
        FOREIGN KEY (project_id_fk) REFERENCES project_tl(project_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_user_project 
        UNIQUE (user_id_fk, project_id_fk)
);

-- ============================================
-- Table: task_user_bridge_tl (Association Tâche-Utilisateur)
-- ============================================
CREATE TABLE task_user_bridge_tl (
    task_user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id_fk BIGINT NOT NULL,
    task_id_fk BIGINT NOT NULL,
    assignment_date DATE DEFAULT (CURRENT_DATE),
    
    INDEX idx_task_bridge_user (user_id_fk),
    INDEX idx_task_bridge_task (task_id_fk),
    
    CONSTRAINT fk_task_bridge_user 
        FOREIGN KEY (user_id_fk) REFERENCES user_tl(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_task_bridge_task 
        FOREIGN KEY (task_id_fk) REFERENCES project_task_tl(task_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_user_task 
        UNIQUE (user_id_fk, task_id_fk)
);
