# 📖 Guide d'Intégration - SGTU Dashboard

Ce guide est destiné à la **personne qui intègre tous les services ensemble**.

## 🎯 Vue d'ensemble

Le dashboard Next.js communique avec **3 services backend** :

1. **Kafka** (Membre 1) - Temps réel pollution/bruit
2. **Service Central** (Membre 2) - Flux & Feux
3. **Service Caméras** (Membre 3) - Analyse vidéo

## ⚙️ Configuration Requise

### Étape 1 : Vérifier que tous les services tournent

Avant de lancer le dashboard, assurez-vous que **TOUS** les services sont actifs :

#### ✅ Membre 1 (Vous)
```bash
# Zookeeper
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

# Kafka Broker
bin\windows\kafka-server-start.bat config\server.properties

# MySQL
# Vérifier que MySQL tourne (port 3306)

# Services Java
# - Service Pollution REST (port 8080)
# - Service Bruit TCP (port 9999 TCP)
# - Service Central (consumers Kafka)
```

#### ✅ Membre 2
```bash
# Service Central (Flux + Feux)
# Port : 9999
# URL : http://localhost:9999/centrale/api
```

#### ✅ Membre 3
```bash
# Service Caméras
# Port : ??? (À DEMANDER AU MEMBRE 3)
# URL : http://localhost:XXXX
```

---

### Étape 2 : Configurer les URLs

**Fichier à modifier :** `sgtu-dashboard/.env.local`
```bash
# ⚠️ MODIFIEZ CETTE LIGNE AVEC L'URL DU MEMBRE 3
NEXT_PUBLIC_SERVICE_CAMERAS_URL=http://localhost:XXXX

# Les autres URLs sont déjà configurées
NEXT_PUBLIC_KAFKA_BROKER=localhost:9092
NEXT_PUBLIC_SERVICE_CENTRAL_URL=http://localhost:9999/centrale/api
```

**Demandez au Membre 3 :**
```
"Sur quel port ton service caméras tourne ?"
Exemples possibles : 8080, 8081, 9090, etc.
```

---

### Étape 3 : Tester les connexions

Avant de lancer le dashboard, testez chaque URL dans votre navigateur :
```bash
# Service Central (Membre 2)
http://localhost:9999/centrale/api/Flux/latest

# Service Caméras (Membre 3) - Remplacer XXXX
http://localhost:XXXX/api/traffic/latest
```

**Résultat attendu :** JSON avec des données

---

### Étape 4 : Lancer le Dashboard
```bash
cd sgtu-dashboard
npm install
npm run dev
```

**Le dashboard sera accessible sur :** http://localhost:3000

---

## ✅ Vérification que tout marche

### Test 1 : Page d'accueil
- URL : http://localhost:3000
- ✅ 6 cartes statistiques affichées
- ✅ Données en temps réel (chiffres qui changent)
- ✅ Aucune erreur dans la console

### Test 2 : Qualité de l'Air
- URL : http://localhost:3000/qualite-air
- ✅ Graphiques pollution/bruit animés
- ✅ Données par zone (Centre, Nord, Sud)

### Test 3 : Gestion du Trafic
- URL : http://localhost:3000/gestion-trafic
- ✅ Flux des 4 routes (nord, sud, est, ouest)
- ✅ Graphique d'évolution

### Test 4 : Contrôle des Feux
- URL : http://localhost:3000/controle-feux
- ✅ Vue de l'intersection
- ✅ Feux verts/rouges animés
- ✅ Boutons de contrôle fonctionnels

### Test 5 : Surveillance Vidéo
- URL : http://localhost:3000/surveillance-video
- ✅ État du trafic (NORMAL/BUSY/CONGESTED)
- ✅ Recommandations affichées

### Test 6 : Alertes
- URL : http://localhost:3000/alertes
- ✅ Alertes de tous les services
- ✅ Filtres fonctionnels
- ✅ Statistiques

---

## 🐛 Dépannage

### Problème : Erreurs "Failed to fetch" dans la console

**Cause :** Un service backend ne répond pas

**Solution :**
1. Vérifiez que le service tourne
2. Vérifiez l'URL dans `.env.local`
3. Testez l'URL directement dans le navigateur

### Problème : Pas de données en temps réel

**Cause :** Kafka n'est pas connecté ou les simulateurs ne tournent pas

**Solution :**
1. Vérifiez Zookeeper (port 2181)
2. Vérifiez Kafka Broker (port 9092)
3. Lancez les simulateurs Java

### Problème : Page blanche

**Cause :** Erreur JavaScript

**Solution :**
1. Ouvrez la console (F12)
2. Regardez les erreurs
3. Vérifiez que `npm run dev` tourne sans erreur

---

## 📞 Contact

En cas de problème, contactez :
- **Membre 1** : Services Kafka
- **Membre 2** : Service Central
- **Membre 3** : Service Caméras

---

## 🎓 Notes pour la Présentation

Le dashboard est **100% fonctionnel** même si certains services ne sont pas lancés :
- Le dashboard affiche des **messages d'erreur gracieux**
- Les **simulations Kafka** fonctionnent toujours
- Chaque section peut être **testée indépendamment**