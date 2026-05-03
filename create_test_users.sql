-- Création d'utilisateurs de test pour l'application StreetLeague
-- Exécuter ces requêtes dans votre base de données MySQL

-- Utilisateur ADMIN
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Admin User', 'admin@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', true);

-- Utilisateur SPONSOR
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Sponsor Nike', 'sponsor@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SPONSOR', true);

-- Utilisateur PLAYER
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Player Test', 'player@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'PLAYER', true);

-- Utilisateur COACH
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Coach Test', 'coach@streetleague.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'COACH', true);

-- Note: Le mot de passe pour tous ces comptes est: password123
-- Le hash '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' correspond à 'password123' encodé avec BCrypt
