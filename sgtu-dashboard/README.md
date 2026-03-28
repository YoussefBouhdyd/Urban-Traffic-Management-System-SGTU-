---

## 🔗 Backend Repository

Le backend Java (Kafka + MySQL) est disponible ici :
👉 https://github.com/Mouhamedou15/sgtu-backend

## 🏗️ Architecture Complète du Système
```
┌─────────────────────────────────────────────────┐
│         BACKEND (Java - Repository 1)           │
│         github.com/Mouhamedou15/sgtu-backend    │
│                                                 │
│  ✅ Service Pollution REST (JAX-RS) - Port 8080 │
│  ✅ Service Bruit TCP (Socket) - Port 9999      │
│  ✅ Service Central (Kafka Consumers)           │
│  ✅ Apache Kafka 3.8.0 (Port 9092)              │
│  ✅ MySQL 8.0 (Port 3306)                       │
└─────────────────────────────────────────────────┘
                      ↓ HTTP/Kafka
┌─────────────────────────────────────────────────┐
│       FRONTEND (Next.js - Repository 2)         │
│       github.com/Mouhamedou15/sgtu-dashboard    │
│                                                 │
│  ✅ Dashboard temps réel (6 pages)              │
│  ✅ WebSocket Kafka (pollution + bruit)         │
│  ✅ HTTP Clients REST (flux + feux + caméras)   │
│  ✅ Zustand State Management                    │
│  ✅ Chart.js Visualisations                     │
└─────────────────────────────────────────────────┘
```

## 📦 Clonage Complet du Projet

### Pour les membres de l'équipe
```bash
# Créer un dossier projet
mkdir sgtu-projet
cd sgtu-projet

# Cloner le backend
git clone https://github.com/Mouhamedou15/sgtu-backend.git

# Cloner le frontend
git clone https://github.com/Mouhamedou15/sgtu-dashboard.git
```

### Installation Backend
```bash
cd sgtu-backend
# Suivre les instructions dans README.md
```

### Installation Frontend
```bash
cd sgtu-dashboard
npm install
cp .env.example .env.local
# Modifier .env.local avec les bonnes URLs
npm run dev
```

## 🔧 Configuration pour l'Intégration

**Fichier à modifier :** `.env.local`
```bash
# Kafka (Membre 1)
NEXT_PUBLIC_KAFKA_BROKER=localhost:9092

# Service Central (Membre 2)
NEXT_PUBLIC_SERVICE_CENTRAL_URL=http://localhost:9999/centrale/api

# Service Caméras (Membre 3)
NEXT_PUBLIC_SERVICE_CAMERAS_URL=http://localhost:8080
```

⚠️ **Important :** Vérifiez avec le Membre 3 le port exact de son service caméras !

## 👥 Membres de l'Équipe

- **Membre 1** : Services Pollution + Bruit (Kafka)
- **Membre 2** : Service Central (Flux + Feux)
- **Membre 3** : Service Caméras (Analyse IA)

**Master 2I2S - Université Mohammed V Rabat**