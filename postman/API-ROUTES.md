# 📚 Index complet des routes API

## 🔗 Clients (`/api/v1/clients`)

| Méthode | Endpoint | Description | Status | Body |
|---------|----------|-------------|--------|------|
| `GET` | `/` | Récupérer tous les clients | 200 | ✗ |
| `GET` | `/?lastName=X` | Filtrer par nom | 200 | ✗ |
| `GET` | `/{id}` | Récupérer un client | 200 | ✗ |
| `POST` | `/` | Créer un client | 201 | ✓ |
| `PUT` | `/{id}` | Modifier un client | 200 | ✓ |
| `DELETE` | `/{id}` | Supprimer un client | 204 | ✗ |

### Exemple de body POST/PUT

```json
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

### Exemple de réponse

```json
{
  "id": 1,
  "firstName": "Jean",
  "lastName": "Dupont",
  "dateOfBirth": "1990-05-15",
  "licenseNumber": "1234567890",
  "address": "123 Rue de la Paix, Paris",
  "email": "jean.dupont@example.com",
  "phone": "0612345678"
}
```

---

## 🚗 Véhicules (`/api/v1/vehicles`)

| Méthode | Endpoint | Description | Status | Body |
|---------|----------|-------------|--------|------|
| `GET` | `/` | Récupérer tous les véhicules | 200 | ✗ |
| `GET` | `/?status=X` | Filtrer par statut | 200 | ✗ |
| `GET` | `/?brand=X` | Filtrer par marque | 200 | ✗ |
| `GET` | `/?status=X&brand=Y` | Filtrer par statut et marque | 200 | ✗ |
| `GET` | `/{id}` | Récupérer un véhicule | 200 | ✗ |
| `POST` | `/` | Créer un véhicule | 201 | ✓ |
| `PUT` | `/{id}` | Modifier un véhicule | 200 | ✓ |
| `POST` | `/{id}/breakdown` | Marquer en panne | 200 | ✗ |
| `POST` | `/{id}/repair` | Marquer réparé | 200 | ✗ |
| `DELETE` | `/{id}` | Supprimer un véhicule | 204 | ✗ |

### Statuts disponibles

- `AVAILABLE` - Disponible
- `RENTED` - Loué
- `BROKEN_DOWN` - En panne

### Exemple de body POST/PUT

```json
{
  "registrationPlate": "AB-123-CD",
  "brand": "Peugeot",
  "model": "3008",
  "motorization": "1.5 BlueHDi 130 ch",
  "color": "Noir Profond",
  "acquisitionDate": "2022-03-15"
}
```

### Exemple de réponse

```json
{
  "id": 1,
  "registrationPlate": "AB-123-CD",
  "brand": "Peugeot",
  "model": "3008",
  "motorization": "1.5 BlueHDi 130 ch",
  "color": "Noir Profond",
  "acquisitionDate": "2022-03-15",
  "status": "AVAILABLE"
}
```

---

## 📋 Contrats (`/api/v1/contracts`)

| Méthode | Endpoint | Description | Status | Body |
|---------|----------|-------------|--------|------|
| `GET` | `/` | Récupérer tous les contrats | 200 | ✗ |
| `GET` | `/?status=X` | Filtrer par statut | 200 | ✗ |
| `GET` | `/?clientId=X` | Filtrer par client | 200 | ✗ |
| `GET` | `/?vehicleId=X` | Filtrer par véhicule | 200 | ✗ |
| `GET` | `/?clientId=X&status=Y` | Filtrer par client et statut | 200 | ✗ |
| `GET` | `/?clientId=X&vehicleId=Y&status=Z` | Filtrer tous les critères | 200 | ✗ |
| `GET` | `/{id}` | Récupérer un contrat | 200 | ✗ |
| `POST` | `/` | Créer un contrat | 201 | ✓ |
| `PUT` | `/{id}` | Modifier un contrat | 200 | ✓ |
| `POST` | `/{id}/approve` | Approuver (PENDING→ONGOING) | 200 | ✗ |
| `POST` | `/{id}/complete` | Terminer (ONGOING→COMPLETED) | 200 | ✗ |
| `POST` | `/{id}/overdue` | Marquer en retard | 200 | ✗ |
| `POST` | `/{id}/cancel` | Annuler | 200 | ✗ |
| `DELETE` | `/{id}` | Supprimer un contrat | 204 | ✗ |

### Statuts disponibles

- `PENDING` - En attente
- `ONGOING` - En cours
- `COMPLETED` - Terminé
- `OVERDUE` - En retard
- `CANCELLED` - Annulé

### Exemple de body POST/PUT

```json
{
  "clientId": 1,
  "vehicleId": 1,
  "startDate": "2025-11-01T10:00:00",
  "endDate": "2025-11-08T17:00:00"
}
```

### Exemple de réponse

```json
{
  "id": 1,
  "clientId": 1,
  "vehicleId": 1,
  "startDate": "2025-11-01T10:00:00",
  "endDate": "2025-11-08T17:00:00",
  "status": "PENDING"
}
```

---

## 🔍 Requêtes GET les plus courantes

### Récupérer tous les clients
```
GET /api/v1/clients
```

### Chercher un client par nom
```
GET /api/v1/clients?lastName=Dupont
```

### Voir les véhicules disponibles
```
GET /api/v1/vehicles?status=AVAILABLE
```

### Voir les Peugeot disponibles
```
GET /api/v1/vehicles?status=AVAILABLE&brand=Peugeot
```

### Voir tous les contrats
```
GET /api/v1/contracts
```

### Voir les contrats en attente
```
GET /api/v1/contracts?status=PENDING
```

### Voir les contrats en cours
```
GET /api/v1/contracts?status=ONGOING
```

### Voir les contrats en retard
```
GET /api/v1/contracts?status=OVERDUE
```

### Voir les contrats d'un client
```
GET /api/v1/contracts?clientId=1
```

### Voir les contrats d'un véhicule
```
GET /api/v1/contracts?vehicleId=1
```

### Voir les contrats en attente d'un client
```
GET /api/v1/contracts?clientId=1&status=PENDING
```

### Voir tous les filtres combinés
```
GET /api/v1/contracts?clientId=1&vehicleId=1&status=ONGOING
```

---

## 📊 Codes de réponse HTTP

| Code | Signification | Cas d'usage |
|------|--------------|-----------|
| `200` | OK | Requête réussie (GET, PUT, POST state-change) |
| `201` | Created | Ressource créée (POST) |
| `204` | No Content | Succès sans contenu (DELETE) |
| `400` | Bad Request | Erreur de validation ou règle métier |
| `404` | Not Found | Ressource inexistante |
| `500` | Server Error | Erreur interne du serveur |

---

## 🔐 Contrôles de validations

### Clients
- ✅ `firstName` et `lastName` obligatoires et non vides
- ✅ `dateOfBirth` au format YYYY-MM-DD
- ✅ `licenseNumber` unique (pas de doublon)
- ✅ Âge >= 18 ans
- ✅ `email` format valide (optionnel)
- ✅ `phone` format valide (optionnel)

### Véhicules
- ✅ `registrationPlate` obligatoire et unique
- ✅ `brand`, `model`, `motorization`, `color` non vides
- ✅ `acquisitionDate` format YYYY-MM-DD

### Contrats
- ✅ `clientId` doit exister
- ✅ `vehicleId` doit exister et ne pas être en panne
- ✅ `startDate` < `endDate`
- ✅ Pas de chevauchement de contrats pour le même véhicule

---

## 🔄 Flux de vie complet

### Cycle client
```
1. POST /clients          → Créer
2. GET /clients/{id}      → Consulter
3. PUT /clients/{id}      → Modifier
4. DELETE /clients/{id}   → Supprimer (cascade sur contrats)
```

### Cycle véhicule
```
1. POST /vehicles         → Créer (statut: AVAILABLE)
2. GET /vehicles/{id}     → Consulter
3. PUT /vehicles/{id}     → Modifier
4. POST /{id}/breakdown   → Panne (annule PENDING)
5. POST /{id}/repair      → Réparation (retour AVAILABLE)
6. DELETE /vehicles/{id}  → Supprimer
```

### Cycle contrat
```
1. POST /contracts              → Créer (statut: PENDING)
2. GET /contracts/{id}          → Consulter
3. PUT /contracts/{id}          → Modifier
4. POST /{id}/approve           → Approuver (PENDING → ONGOING)
5. POST /{id}/complete          → Terminer (ONGOING → COMPLETED)
   OU
   POST /{id}/overdue           → Retard (ONGOING → OVERDUE) [scheduler]
   POST /{id}/cancel            → Annuler
6. DELETE /contracts/{id}       → Supprimer
```

---

## 💡 Combinaisons de filtres populaires

### Rechercher les locations actives
```
GET /api/v1/contracts?status=ONGOING
```

### Trouver un client et ses locations
```
# 1. Trouver le client
GET /api/v1/clients?lastName=Dupont

# 2. Récupérer ses contrats
GET /api/v1/contracts?clientId=1
```

### Voir la disponibilité d'une marque
```
GET /api/v1/vehicles?status=AVAILABLE&brand=Peugeot
```

### Récupérer les locations en conflit
```
GET /api/v1/contracts?status=OVERDUE
GET /api/v1/contracts?status=PENDING
# Comparer les dates pour identifier les conflits
```

---

## 📈 Performance des requêtes

Toutes les requêtes GET utilisent des **requêtes SQL optimisées** :
- Index sur `status`, `clientId`, `vehicleId`, `endDate`
- DISTINCT pour éviter les doublons
- Filtrage à la base de données (pas en application)

**Résultat** : Performances O(1) même avec millions de données

---

## 🚀 Prochaines étapes après import

1. ✅ Importer la collection dans Postman
2. ✅ Créer un client (POST /clients)
3. ✅ Créer un véhicule (POST /vehicles)
4. ✅ Créer un contrat (POST /contracts)
5. ✅ Approuver le contrat (POST /contracts/{id}/approve)
6. ✅ Vérifier avec les GET et filtres
7. ✅ Consulter les logs pour voir les événements

---

**Version** : 1.0  
**Dernière mise à jour** : 28 Octobre 2025  
**Base de données** : PostgreSQL 15+  
**Framew ork** : Spring Boot 3.5.7  
