-- ============================================================================
-- SCRIPT SQL : SYSTÈME DE GESTION DU TRAFIC URBAIN
-- ============================================================================
-- 
-- Projet : Système Distribué de Gestion du Trafic et de la Pollution Urbaine
-- Auteur : Mouhamedou
-- Date : 2026-03-15
-- Formation : Master 2I2S - UM5 Rabat
-- 
-- Technologies : Java, Kafka, MySQL, JAX-RS, TCP Sockets
-- 
-- ============================================================================

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS traffic_pollution_bruit
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE traffic_pollution_bruit;

-- ============================================================================
-- TABLE 1 : POLLUTION
-- ============================================================================
-- Stocke toutes les données de pollution reçues depuis le topic Kafka
-- Source : Service Pollution REST (JAX-RS) → pollution-topic → Service Central
-- ============================================================================

CREATE TABLE pollution (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Identifiant unique',
    zone_id VARCHAR(50) NOT NULL COMMENT 'Zone géographique (Zone_Centre, Zone_Nord, Zone_Sud)',
    niveau_co2 DECIMAL(10,2) NOT NULL COMMENT 'Niveau de CO2 en µg/m³',
    timestamp VARCHAR(50) NOT NULL COMMENT 'Horodatage de la mesure (format ISO 8601)',
    date_insertion TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date d\'insertion en base de données'
) ENGINE=InnoDB COMMENT='Données de pollution (CO2)';

-- Index pour améliorer les performances
CREATE INDEX idx_zone_pollution ON pollution(zone_id);
CREATE INDEX idx_timestamp_pollution ON pollution(timestamp);

-- ============================================================================
-- TABLE 2 : BRUIT
-- ============================================================================
-- Stocke toutes les données de bruit reçues depuis le topic Kafka
-- Source : Service Bruit TCP (Socket) → bruit-topic → Service Central
-- ============================================================================

CREATE TABLE bruit (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Identifiant unique',
    zone_id VARCHAR(50) NOT NULL COMMENT 'Zone géographique',
    niveau_decibels DECIMAL(10,2) NOT NULL COMMENT 'Niveau de bruit en dB',
    timestamp VARCHAR(50) NOT NULL COMMENT 'Horodatage de la mesure (format ISO 8601)',
    date_insertion TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date d\'insertion en base de données'
) ENGINE=InnoDB COMMENT='Données de bruit (décibels)';

-- Index pour améliorer les performances
CREATE INDEX idx_zone_bruit ON bruit(zone_id);
CREATE INDEX idx_timestamp_bruit ON bruit(timestamp);

-- ============================================================================
-- TABLE 3 : ALERTES
-- ============================================================================
-- Stocke les alertes générées automatiquement par le Service Central
-- Seuils : Pollution > 80 µg/m³ ou Bruit > 85 dB
-- ============================================================================

CREATE TABLE alertes (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Identifiant unique',
    type_alerte VARCHAR(50) NOT NULL COMMENT 'Type : POLLUTION ou BRUIT',
    zone_id VARCHAR(50) NOT NULL COMMENT 'Zone géographique concernée',
    niveau_gravite VARCHAR(20) NOT NULL COMMENT 'Gravité : HAUTE, MOYENNE, BASSE',
    message TEXT COMMENT 'Message descriptif de l\'alerte',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date de création de l\'alerte'
) ENGINE=InnoDB COMMENT='Alertes générées automatiquement';

-- Index pour améliorer les performances
CREATE INDEX idx_type_alerte ON alertes(type_alerte);
CREATE INDEX idx_zone_alerte ON alertes(zone_id);
CREATE INDEX idx_gravite ON alertes(niveau_gravite);

-- ============================================================================
-- TABLE 4 : RECOMMANDATIONS
-- ============================================================================
-- Stocke les recommandations associées aux alertes
-- Relation : Une alerte → Plusieurs recommandations possibles
-- ============================================================================

CREATE TABLE recommandations (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Identifiant unique',
    alerte_id INT COMMENT 'Référence vers l\'alerte',
    action_recommandee TEXT NOT NULL COMMENT 'Action recommandée',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date de création',
    FOREIGN KEY (alerte_id) REFERENCES alertes(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Recommandations liées aux alertes';

-- Index pour améliorer les performances
CREATE INDEX idx_alerte_recommandation ON recommandations(alerte_id);

-- ============================================================================
-- VÉRIFICATION
-- ============================================================================

SHOW TABLES;

