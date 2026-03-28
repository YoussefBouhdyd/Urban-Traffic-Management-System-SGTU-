# 🏙️ SGTU Backend - Système de Gestion du Trafic Urbain

Backend distribué en Java pour la gestion intelligente du trafic urbain à Rabat.

## 📋 Architecture

### Services Développés

- **Service Pollution REST** (JAX-RS Jersey) - Port 8080
- **Simulateur Pollution** (HTTP Client)
- **Service Bruit TCP** (Socket ServerSocket) - Port 9999
- **Simulateur Bruit** (Socket Client)
- **Service Central** (Kafka Consumers + MySQL)

### Technologies

- **Java** : 21.0.9
- **Apache Kafka** : 3.8.0
- **MySQL** : 8.0.41
- **JAX-RS** : Jersey 3.1.3
- **Maven** : Build tool
- **Eclipse IDE** : 2026-03

## 🚀 Installation

### Prérequis

- Java JDK 21+
- Apache Kafka 3.8.0 (installé dans `C:\kafka`)
- MySQL 8.0 (credentials: root/root123)
- Eclipse IDE

### Base de Données
```sql
CREATE DATABASE traffic_pollution_bruit;

-- Tables : pollution, bruit, alertes, recommandations
```

### Lancement

#### 1. Démarrer Zookeeper
```cmd
C:\kafka\bin\windows\zookeeper-server-start.bat C:\kafka\config\zookeeper.properties
```

#### 2. Démarrer Kafka Broker
```cmd
C:\kafka\bin\windows\kafka-server-start.bat C:\kafka\config\server.properties
```

#### 3. Démarrer MySQL

Vérifier que MySQL tourne sur le port 3306

#### 4. Lancer les Services dans Eclipse

Dans l'ordre :
1. `Main.java` (Service Pollution REST)
2. `SimulateurPollution.java`
3. `ServerTCP.java` (Service Bruit TCP)
4. `SimulateurBruit.java`
5. `ServiceCentral.java`

## 📁 Structure du Projet
```
ProjetJava/
├── service-pollution-rest/      # Service REST pollution (JAX-RS)
│   ├── src/main/java/
│   └── pom.xml
├── simulateur-pollution/        # Simulateur HTTP pollution
│   ├── src/main/java/
│   └── pom.xml
├── service-bruit-tcp/          # Service TCP bruit (Socket)
│   ├── src/main/java/
│   └── pom.xml
├── simulateur-bruit/           # Simulateur TCP bruit
│   ├── src/main/java/
│   └── pom.xml
└── service-central/            # Consumers Kafka + MySQL
    ├── src/main/java/
    └── pom.xml
```

## 🔌 Ports Utilisés

| Service | Port |
|---------|------|
| Service Pollution REST | 8080 |
| Service Bruit TCP | 9999 |
| Kafka Broker | 9092 |
| Zookeeper | 2181 |
| MySQL | 3306 |

## 📊 Topics Kafka

- `pollution-topic` : Données de pollution (CO2)
- `bruit-topic` : Données de bruit (décibels)

## 🗄️ Base de Données

**Database** : `traffic_pollution_bruit`

**Tables** :
- `pollution` : Données pollution par zone
- `bruit` : Données bruit par zone
- `alertes` : Alertes générées automatiquement
- `recommandations` : Recommandations système

## 👥 Équipe

**Membre 1** : Services Pollution + Bruit + Service Central

Master Ingénierie Informatique et Sécurité des Systèmes (2I2S)
Université Mohammed V - Rabat, Maroc

## 🔗 Dashboard Frontend

Le dashboard Next.js est disponible dans un repository séparé :
👉 https://github.com/Mouhamedou15/sgtu-dashboard

## 📝 License

Projet académique - Master 2I2S - UM5 Rabat