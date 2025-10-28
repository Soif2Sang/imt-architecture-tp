# 🚀 Guide de Démarrage Rapide

## Prérequis

- **Java 25+** installé
- **PostgreSQL 15+** en cours d'exécution
- **Spring Boot 3.5.7** (inclus dans le projet)
- **Postman** (ou équivalent pour tester l'API)

---

## 1️⃣ Configuration de la base de données

### Démarrer PostgreSQL (Docker)

```bash
docker run -d \
  --name postgres-tp \
  -e POSTGRES_USER=tp_user \
  -e POSTGRES_PASSWORD=tp_password123 \
  -e POSTGRES_DB=tp_db \
  -p 5432:5432 \
  postgres:15
```

### Vérifier la connexion

```bash
psql -U tp_user -d tp_db -h localhost -c "SELECT version();"
```

---

## 2️⃣ Démarrer l'application Spring Boot

### Compiler le projet

```bash
cd c:\Users\maxence\Documents\cours\an2\archi\tp
.\mvnw.cmd clean compile
```

### Lancer l'application

```bash
.\mvnw.cmd spring-boot:run
```

### Vérifier que l'API est accessible

```bash
curl http://localhost:8080/api/v1/clients
# Devrait retourner : []
```

---

## 3️⃣ Importer la collection Postman

1. Ouvrir **Postman**
2. Cliquer sur **File** → **Import**
3. Sélectionner le fichier : `postman/tp-api-collection.json`
4. La collection s'import avec :
   - 3 dossiers (Clients, Véhicules, Contrats)
   - 30+ requêtes pré-configurées
   - Variables d'environnement

---

## 4️⃣ Premier test en 5 minutes

### Étape 1 : Créer un client
```
POST /api/v1/clients
{
  "firstName": "Jean",
  "lastName": "Dupont",
  "dateOfBirth": "1990-05-15",
  "licenseNumber": "1234567890",
  "address": "123 Rue de la Paix, Paris",
  "email": "jean.dupont@example.com",
  "phone": "0612345678"
}
```
✅ Réponse : Client créé avec `id: 1`

### Étape 2 : Copier l'ID du client
- Aller aux **Variables** de la collection
- Remplacer `client_id` par `1`

### Étape 3 : Créer un véhicule
```
POST /api/v1/vehicles
{
  "registrationPlate": "AB-123-CD",
  "brand": "Peugeot",
  "model": "3008",
  "motorization": "1.5 BlueHDi 130 ch",
  "color": "Noir Profond",
  "acquisitionDate": "2022-03-15"
}
```
✅ Réponse : Véhicule créé avec `id: 1`

### Étape 4 : Copier l'ID du véhicule
- Aller aux **Variables** de la collection
- Remplacer `vehicle_id` par `1`

### Étape 5 : Créer une location
```
POST /api/v1/contracts
{
  "clientId": 1,
  "vehicleId": 1,
  "startDate": "2025-11-01T10:00:00",
  "endDate": "2025-11-08T17:00:00"
}
```
✅ Réponse : Contrat créé avec `id: 1` et `status: PENDING`

### Étape 6 : Approuver la location
```
POST /api/v1/contracts/1/approve
```
✅ Réponse : Contrat avec `status: ONGOING`

### Étape 7 : Vérifier les locations en cours
```
GET /api/v1/contracts?status=ONGOING
```
✅ Réponse : Le contrat créé s'affiche avec le statut ONGOING

---

## 5️⃣ Tester les fonctionnalités avancées

### Tester la panne automatique
```bash
# 1. Créer un 2e contrat en attente
POST /api/v1/contracts
{
  "clientId": 1,
  "vehicleId": 1,
  "startDate": "2025-11-10T10:00:00",
  "endDate": "2025-11-15T17:00:00"
}

# 2. Marquer le véhicule en panne
POST /api/v1/vehicles/1/breakdown

# 3. Vérifier que les contrats PENDING ont été annulés
GET /api/v1/contracts?status=CANCELLED
```

### Tester le filtrage avancé
```bash
# Voir les Peugeot disponibles
GET /api/v1/vehicles?status=AVAILABLE&brand=Peugeot

# Voir les contrats en attente d'un client
GET /api/v1/contracts?clientId=1&status=PENDING

# Voir tous les filtres combinés
GET /api/v1/contracts?clientId=1&vehicleId=1&status=ONGOING
```

### Tester le scheduler (en local)
```bash
# Le scheduler s'exécute à minuit
# Pour le tester manuellement, modifiez la cron dans :
# ContractOverdueScheduler.java

# Ligne 42 : @Scheduled(cron = "0 0 0 * * *")
# Changer à : @Scheduled(cron = "*/10 * * * * *") # Toutes les 10 secondes

# Puis redémarrer l'application
.\mvnw.cmd spring-boot:run

# Vérifier les logs pour voir les contrats en retard
```

---

## 📁 Structure du dossier Postman

```
postman/
├── tp-api-collection.json      # Collection Postman (à importer)
├── README.md                   # Guide complet d'utilisation
├── EXAMPLES.md                 # Exemples de requêtes/réponses
└── BUSINESS-RULES.md          # Documentation des règles métier
```

---

## 🔧 Troubleshooting

### ❌ Erreur : "Connection refused" (127.0.0.1:5432)
**Solution** : PostgreSQL n'est pas en cours d'exécution
```bash
docker start postgres-tp
```

### ❌ Erreur : "401 Unauthorized"
**Solution** : L'API n'a pas d'authentification, vérifier les identifiants PostgreSQL
```bash
docker logs postgres-tp
```

### ❌ Erreur : "404 Not Found"
**Solution** : Vérifier l'ID dans la requête
```bash
GET /api/v1/clients/999999  # ID inexistant
```

### ❌ Erreur : "400 Bad Request"
**Solution** : Vérifier le format JSON et les règles métier
- Voir le message d'erreur pour connaître la règle violée

### ❌ L'API retourne []
**Solution** : C'est normal ! La première requête retourne une liste vide
```bash
POST /api/v1/clients  # Créer des données d'abord
GET /api/v1/clients   # Puis récupérer les données
```

---

## 📊 Fichiers importants du projet

```
src/main/java/
├── api/
│   ├── dto/              # DTOs Request/Response
│   └── rest/             # Controllers REST
├── business/             # Logique métier
│   ├── client/services/  # ClientService
│   ├── vehicle/services/ # VehicleService
│   └── contract/services/# ContractService
├── common/
│   ├── enums/            # ContractStatus, VehicleStatus
│   └── exceptions/       # BusinessException
└── infrastructure/
    ├── db/
    │   ├── entity/       # Entities JPA
    │   ├── mapper/       # Persistence Mappers
    │   └── repository/   # Repositories Spring Data
    ├── event/            # Événements et Handlers
    └── scheduler/        # Scheduler quotidien

resources/
└── application.properties # Configuration
```

---

## 📝 Logs importants à surveiller

### Au démarrage
```
Hibernate: create table client_entity...
Hibernate: create table vehicle_entity...
Hibernate: create table contract_entity...
Hikari Connection Pool initialized
Started TpApplication in X seconds
```

### Lors d'une panne de véhicule
```
⚠️ Traitement de 2 contrat(s) en attente pour la rupture du véhicule 1
INFO Annulation du contrat 2 en attente pour le véhicule en panne 1
WARN 2 contrat(s) annulé(s) suite à la panne du véhicule 1
```

### À minuit (scheduler)
```
=== Début du traitement quotidien des contrats en retard ===
⚠️ Traitement de 3 contrat(s) en retard
Passage du contrat 1 au statut OVERDUE (endDate: 2025-10-26T18:00:00)
=== Fin du traitement quotidien des contrats en retard ===
```

---

## 🎯 Cas d'usage complets

### Scénario 1 : Créer une location complète
1. ✅ POST /clients - Créer client
2. ✅ POST /vehicles - Créer véhicule  
3. ✅ POST /contracts - Créer contrat
4. ✅ POST /contracts/{id}/approve - Approuver
5. ✅ GET /contracts?status=ONGOING - Vérifier

### Scénario 2 : Tester les filtres
1. ✅ POST /vehicles - Créer plusieurs véhicules
2. ✅ GET /vehicles?status=AVAILABLE - Filtrer par statut
3. ✅ GET /vehicles?brand=Peugeot - Filtrer par marque
4. ✅ GET /vehicles?status=AVAILABLE&brand=Peugeot - Combiner

### Scénario 3 : Tester la panne
1. ✅ POST /contracts - Créer 2 contrats en attente
2. ✅ POST /vehicles/{id}/breakdown - Marquer en panne
3. ✅ GET /contracts?status=CANCELLED - Voir les annulations
4. ✅ POST /vehicles/{id}/repair - Réparer le véhicule

---

## 📞 Besoin d'aide ?

Consulter les fichiers de documentation :
- **README.md** - Guide complet d'utilisation
- **EXAMPLES.md** - Exemples de requêtes/réponses
- **BUSINESS-RULES.md** - Règles métier et diagrammes
- **Logs de l'application** - Messages d'erreur détaillés

---

## ✅ Checklist de démarrage

- [ ] PostgreSQL est en cours d'exécution
- [ ] Projet Maven a compilé sans erreurs
- [ ] Application Spring Boot démarre correctement
- [ ] API accessible sur `http://localhost:8080`
- [ ] Collection Postman importée
- [ ] Premier test (créer un client) réussi
- [ ] Filtres fonctionnent correctement
- [ ] Panne de véhicule annule les contrats
- [ ] Scheduler configuré (optionnel)
- [ ] Tous les logs s'affichent correctement

---

Bon développement ! 🚀
