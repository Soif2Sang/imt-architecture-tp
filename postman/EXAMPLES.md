# Exemples de Réponses API

## 🟢 Succès (2xx)

### 201 Created - Créer un client

**Request :**
```http
POST /api/v1/clients HTTP/1.1
Content-Type: application/json

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

**Response :**
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

### 200 OK - Récupérer tous les clients

**Request :**
```http
GET /api/v1/clients HTTP/1.1
```

**Response :**
```json
[
  {
    "id": 1,
    "firstName": "Jean",
    "lastName": "Dupont",
    "dateOfBirth": "1990-05-15",
    "licenseNumber": "1234567890",
    "address": "123 Rue de la Paix, Paris",
    "email": "jean.dupont@example.com",
    "phone": "0612345678"
  },
  {
    "id": 2,
    "firstName": "Marie",
    "lastName": "Martin",
    "dateOfBirth": "1985-08-20",
    "licenseNumber": "9876543210",
    "address": "456 Avenue de la Liberté, Lyon",
    "email": "marie.martin@example.com",
    "phone": "0687654321"
  }
]
```

---

### 200 OK - Récupérer un véhicule par ID

**Request :**
```http
GET /api/v1/vehicles/1 HTTP/1.1
```

**Response :**
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

### 200 OK - Filtrer les véhicules disponibles

**Request :**
```http
GET /api/v1/vehicles?status=AVAILABLE HTTP/1.1
```

**Response :**
```json
[
  {
    "id": 1,
    "registrationPlate": "AB-123-CD",
    "brand": "Peugeot",
    "model": "3008",
    "motorization": "1.5 BlueHDi 130 ch",
    "color": "Noir Profond",
    "acquisitionDate": "2022-03-15",
    "status": "AVAILABLE"
  },
  {
    "id": 3,
    "registrationPlate": "XY-789-ZA",
    "brand": "Renault",
    "model": "Scenic",
    "motorization": "1.3 TCe 140 ch",
    "color": "Blanc Glacier",
    "acquisitionDate": "2023-01-10",
    "status": "AVAILABLE"
  }
]
```

---

### 201 Created - Créer un contrat

**Request :**
```http
POST /api/v1/contracts HTTP/1.1
Content-Type: application/json

{
  "clientId": 1,
  "vehicleId": 1,
  "startDate": "2025-11-01T10:00:00",
  "endDate": "2025-11-08T17:00:00"
}
```

**Response :**
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

### 200 OK - Filtrer les contrats en cours

**Request :**
```http
GET /api/v1/contracts?status=ONGOING HTTP/1.1
```

**Response :**
```json
[
  {
    "id": 1,
    "clientId": 1,
    "vehicleId": 1,
    "startDate": "2025-11-01T10:00:00",
    "endDate": "2025-11-08T17:00:00",
    "status": "ONGOING"
  }
]
```

---

### 200 OK - Filtrer par multiples critères

**Request :**
```http
GET /api/v1/contracts?clientId=1&vehicleId=1&status=ONGOING HTTP/1.1
```

**Response :**
```json
[
  {
    "id": 1,
    "clientId": 1,
    "vehicleId": 1,
    "startDate": "2025-11-01T10:00:00",
    "endDate": "2025-11-08T17:00:00",
    "status": "ONGOING"
  }
]
```

---

### 200 OK - Approuver un contrat

**Request :**
```http
POST /api/v1/contracts/1/approve HTTP/1.1
```

**Response :**
```json
{
  "id": 1,
  "clientId": 1,
  "vehicleId": 1,
  "startDate": "2025-11-01T10:00:00",
  "endDate": "2025-11-08T17:00:00",
  "status": "ONGOING"
}
```

---

### 200 OK - Marquer un véhicule en panne

**Request :**
```http
POST /api/v1/vehicles/1/breakdown HTTP/1.1
```

**Response :**
```json
{
  "id": 1,
  "registrationPlate": "AB-123-CD",
  "brand": "Peugeot",
  "model": "3008",
  "motorization": "1.5 BlueHDi 130 ch",
  "color": "Noir Profond",
  "acquisitionDate": "2022-03-15",
  "status": "BROKEN_DOWN"
}
```

---

### 204 No Content - Supprimer un client

**Request :**
```http
DELETE /api/v1/clients/1 HTTP/1.1
```

**Response :** (vide)

---

## 🔴 Erreurs (4xx)

### 400 Bad Request - Validation échouée

**Request :**
```http
POST /api/v1/clients HTTP/1.1
Content-Type: application/json

{
  "firstName": "Jean",
  "lastName": "Dupont"
  // Données manquantes : dateOfBirth, licenseNumber, address, email, phone
}
```

**Response :**
```json
{
  "message": "Erreur de validation : le numero de permis ne peut pas être vide"
}
```

---

### 404 Not Found - Ressource non trouvée

**Request :**
```http
GET /api/v1/clients/99999 HTTP/1.1
```

**Response :**
```json
{
  "message": "Le client avec l'ID 99999 n'existe pas"
}
```

---

### 400 Bad Request - Chevauchement de contrats

**Request :**
```http
POST /api/v1/contracts HTTP/1.1
Content-Type: application/json

{
  "clientId": 1,
  "vehicleId": 1,
  "startDate": "2025-11-03T10:00:00",
  "endDate": "2025-11-10T17:00:00"
}
```

**Response :**
```json
{
  "message": "Erreur métier : Le véhicule 1 est déjà réservé pour cette période. Contrats en conflit : [1]"
}
```

---

### 400 Bad Request - Plaque d'immatriculation déjà existante

**Request :**
```http
POST /api/v1/vehicles HTTP/1.1
Content-Type: application/json

{
  "registrationPlate": "AB-123-CD",
  "brand": "Renault",
  "model": "Clio",
  "motorization": "1.0 TCe 100 ch",
  "color": "Blanc",
  "acquisitionDate": "2023-06-20"
}
```

**Response :**
```json
{
  "message": "Erreur métier : Un véhicule avec la plaque d'immatriculation 'AB-123-CD' existe déjà"
}
```

---

### 400 Bad Request - Numéro de permis déjà existant

**Request :**
```http
PUT /api/v1/clients/1 HTTP/1.1
Content-Type: application/json

{
  "firstName": "Jean-Pierre",
  "lastName": "Dupont",
  "dateOfBirth": "1990-05-15",
  "licenseNumber": "9876543210",
  "address": "123 Rue de la Paix, Paris",
  "email": "jean-pierre.dupont@example.com",
  "phone": "0612345678"
}
```

**Response :**
```json
{
  "message": "Erreur métier : Un client avec le numéro de permis '9876543210' existe déjà"
}
```

---

### 400 Bad Request - Véhicule en panne annule les contrats en attente

**Request :**
```http
POST /api/v1/vehicles/1/breakdown HTTP/1.1
```

**Response :**
```json
{
  "id": 1,
  "registrationPlate": "AB-123-CD",
  "brand": "Peugeot",
  "model": "3008",
  "motorization": "1.5 BlueHDi 130 ch",
  "color": "Noir Profond",
  "acquisitionDate": "2022-03-15",
  "status": "BROKEN_DOWN"
}
```

**Logs côté serveur :**
```
WARN  - Traitement de 2 contrat(s) en attente pour la rupture du véhicule 1
INFO  - Annulation du contrat 2 en attente pour le véhicule en panne 1
INFO  - Annulation du contrat 3 en attente pour le véhicule en panne 1
WARN  - 2 contrat(s) annulé(s) suite à la panne du véhicule 1
```

---

## 📊 Flux de vie d'un contrat

### 1. Création (PENDING)
```
POST /api/v1/contracts
→ { status: "PENDING" }
```

### 2. Approbation (PENDING → ONGOING)
```
POST /api/v1/contracts/1/approve
→ { status: "ONGOING" }
```

### 3. Termination normale (ONGOING → COMPLETED)
```
POST /api/v1/contracts/1/complete
→ { status: "COMPLETED" }
```

### 3 bis. Retard détecté (ONGOING → OVERDUE)
```
[Scheduler exécuté à minuit]
→ { status: "OVERDUE" }
```

### 4 bis. Annulation en retard (OVERDUE → CANCELLED)
```
[Scheduler exécuté à minuit si conflit avec PENDING]
→ { status: "CANCELLED" }
```

### Autres : Annulation manuelle (ANY → CANCELLED)
```
POST /api/v1/contracts/1/cancel
→ { status: "CANCELLED" }
```

---

## 🔄 Flux de vie d'un véhicule

### 1. Création (AVAILABLE)
```
POST /api/v1/vehicles
→ { status: "AVAILABLE" }
```

### 2. Loué (AVAILABLE → RENTED)
```
[ImplicitementChangé lors de la création d'un contrat ONGOING]
```

### 3. Disponible à nouveau (RENTED → AVAILABLE)
```
[Implicitement changé lors de la complétion d'un contrat]
```

### En panne (ANY → BROKEN_DOWN)
```
POST /api/v1/vehicles/1/breakdown
→ { status: "BROKEN_DOWN" }
```

### Réparé (BROKEN_DOWN → AVAILABLE)
```
POST /api/v1/vehicles/1/repair
→ { status: "AVAILABLE" }
```
