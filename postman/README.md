# Collection Postman - TP Gestion de Location de Véhicules

Cette collection Postman contient toutes les requêtes API pour tester l'application de gestion de location de véhicules.

## 📋 Installation

### 1. Importer la collection dans Postman
- Ouvrir Postman
- Cliquer sur "File" → "Import"
- Sélectionner le fichier `tp-api-collection.json`
- La collection sera importée avec toutes les requêtes

### 2. Configurer l'URL de base
La collection utilise une variable `{{base_url}}` définie par défaut à `http://localhost:8080`

Pour modifier :
1. Ouvrir la collection
2. Aller à l'onglet "Variables"
3. Modifier la valeur de `base_url` si nécessaire

## 🚀 Utilisation

### Variables disponibles
- `base_url` : URL de base de l'API (défaut: `http://localhost:8080`)
- `client_id` : ID du client à tester (défaut: `1`)
- `vehicle_id` : ID du véhicule à tester (défaut: `1`)
- `contract_id` : ID du contrat à tester (défaut: `1`)

### Mettre à jour les variables lors de tests
1. Exécuter une requête qui retourne un ID (ex: créer un client)
2. Copier l'ID reçu
3. Mettre à jour la variable correspondante dans les Variables de la collection

## 📁 Structure des requêtes

### Clients (`/api/v1/clients`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/` | Créer un client |
| GET | `/` | Récupérer tous les clients |
| GET | `/?lastName=X` | Filtrer les clients par nom |
| GET | `/{id}` | Récupérer un client par ID |
| PUT | `/{id}` | Modifier un client |
| DELETE | `/{id}` | Supprimer un client |

### Véhicules (`/api/v1/vehicles`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/` | Créer un véhicule |
| GET | `/` | Récupérer tous les véhicules |
| GET | `/?status=X` | Filtrer par statut (AVAILABLE, RENTED, BROKEN_DOWN) |
| GET | `/?brand=X` | Filtrer par marque |
| GET | `/?status=X&brand=Y` | Filtrer par statut ET marque |
| GET | `/{id}` | Récupérer un véhicule par ID |
| PUT | `/{id}` | Modifier un véhicule |
| POST | `/{id}/breakdown` | Marquer le véhicule en panne |
| POST | `/{id}/repair` | Marquer le véhicule réparé |
| DELETE | `/{id}` | Supprimer un véhicule |

### Contrats (`/api/v1/contracts`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/` | Créer un contrat |
| GET | `/` | Récupérer tous les contrats |
| GET | `/?status=X` | Filtrer par statut (PENDING, ONGOING, COMPLETED, OVERDUE, CANCELLED) |
| GET | `/?clientId=X` | Filtrer par client |
| GET | `/?vehicleId=X` | Filtrer par véhicule |
| GET | `/?clientId=X&status=Y` | Filtrer par client ET statut |
| GET | `/?clientId=X&vehicleId=Y&status=Z` | Filtrer par tous les critères |
| GET | `/{id}` | Récupérer un contrat par ID |
| PUT | `/{id}` | Modifier un contrat |
| POST | `/{id}/approve` | Approuver un contrat (PENDING → ONGOING) |
| POST | `/{id}/complete` | Terminer un contrat (ONGOING → COMPLETED) |
| POST | `/{id}/overdue` | Marquer en retard (→ OVERDUE) |
| POST | `/{id}/cancel` | Annuler un contrat (→ CANCELLED) |
| DELETE | `/{id}` | Supprimer un contrat |

## 💡 Exemples de scénarios de test

### Scénario 1 : Créer une location simple
1. **POST /clients** - Créer un client
   - Copier l'ID reçu dans `client_id`
2. **POST /vehicles** - Créer un véhicule
   - Copier l'ID reçu dans `vehicle_id`
3. **POST /contracts** - Créer un contrat avec les IDs
   - Copier l'ID reçu dans `contract_id`
4. **POST /contracts/{{contract_id}}/approve** - Approuver la location
5. **GET /contracts?status=ONGOING** - Vérifier la location active

### Scénario 2 : Tester le filtrage
1. **GET /vehicles?status=AVAILABLE** - Voir les véhicules disponibles
2. **GET /vehicles?brand=Peugeot** - Voir les Peugeot
3. **GET /vehicles?status=AVAILABLE&brand=Peugeot** - Voir les Peugeot disponibles
4. **GET /contracts?clientId=1&status=PENDING** - Voir les contrats en attente du client 1

### Scénario 3 : Tester la gestion de panne
1. **POST /vehicles/{{vehicle_id}}/breakdown** - Marquer un véhicule en panne
   - Cela annulera tous les contrats PENDING de ce véhicule
2. **GET /contracts?status=CANCELLED** - Vérifier les contrats annulés
3. **POST /vehicles/{{vehicle_id}}/repair** - Marquer le véhicule réparé

## 📊 Formats des réponses

### Réponse Client
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

### Réponse Véhicule
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

### Réponse Contrat
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

## ⚙️ Configuration

### Ports et URL
- URL par défaut : `http://localhost:8080`
- Port Spring Boot : `8080`
- Base de données : PostgreSQL sur `localhost:5432`

### Statuts disponibles

**Clients :** N/A (aucun statut)

**Véhicules :**
- `AVAILABLE` - Disponible
- `RENTED` - Loué
- `BROKEN_DOWN` - En panne

**Contrats :**
- `PENDING` - En attente
- `ONGOING` - En cours
- `COMPLETED` - Terminé
- `OVERDUE` - En retard
- `CANCELLED` - Annulé

## 🔧 Dépannage

### L'API ne répond pas
- Vérifier que le serveur Spring Boot est démarré
- Vérifier l'URL de base (défaut: `http://localhost:8080`)
- Vérifier que PostgreSQL est en cours d'exécution

### Erreur 400 - Bad Request
- Vérifier le format JSON du body
- Vérifier que tous les champs requis sont fournis
- Consulter le message d'erreur de la réponse

### Erreur 404 - Not Found
- Vérifier l'ID du client/véhicule/contrat
- Vérifier l'URL et la méthode HTTP

### Erreur 409 - Conflict
- Vérifier les validations métier (ex: chevauchement de contrats)
- Vérifier les règles de gestion (ex: plaque d'immatriculation unique)

## 📝 Notes supplémentaires

- Les variables sont réinitialisées à chaque import de la collection
- Les requêtes sont pré-remplies avec des exemples réalistes
- Vous pouvez dupliquer les dossiers pour tester plusieurs scénarios en parallèle
- Les timestamps utilisent le format ISO 8601 (YYYY-MM-DDTHH:MM:SS)

## 🚀 Prochaines étapes

1. Importer la collection dans Postman
2. Vérifier que l'API est accessible sur `http://localhost:8080`
3. Commencer par le scénario 1 pour créer des données
4. Tester les filtres et les différentes opérations
5. Vérifier les logs de l'application pour voir les événements déclencher
