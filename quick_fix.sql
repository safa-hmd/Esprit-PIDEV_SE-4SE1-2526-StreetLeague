-- UTILISATEURS ORIGINAUX - COPIEZ-COLLEZ CES REQUÊTES DANS VOTRE BASE DE DONNÉES

-- COMPTE ADMIN EXISTANT
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Admin User', 'admin@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', true);

-- COMPTE SPONSOR EXISTANT  
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Sponsor Nike', 'sponsor@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SPONSOR', true);

-- COMPTE PLAYER EXISTANT
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Player Test', 'player@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PLAYER', true);

-- COMPTE COACH EXISTANT
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Coach Test', 'coach@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'COACH', true);

-- MOT DE PASSE POUR TOUS : password123
