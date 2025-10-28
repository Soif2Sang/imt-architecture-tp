# Documentation des Statuts et Règles Métier

## 🚗 Statuts des Véhicules

### AVAILABLE (Disponible)
- **Description** : Le véhicule est disponible pour la location
- **Transitions possibles** :
  - `AVAILABLE` → `RENTED` (lors d'une location active)
  - `AVAILABLE` → `BROKEN_DOWN` (signalement d'une panne)
- **Nombre de contrats** : 0 contrat ONGOING
- **Actions possibles** :
  - POST `/vehicles/{id}/breakdown` → Marquer en panne
  - POST `/vehicles/{id}` → Créer une nouvelle location

---

### RENTED (Loué)
- **Description** : Le véhicule est actuellement loué
- **Transitions possibles** :
  - `RENTED` → `AVAILABLE` (fin de la location, contrat COMPLETED)
  - `RENTED` → `BROKEN_DOWN` (panne pendant la location)
- **Nombre de contrats** : 1 contrat ONGOING
- **Actions possibles** :
  - Aucune action directe (le statut change automatiquement)
  - POST `/vehicles/{id}/breakdown` → Marquer en panne

---

### BROKEN_DOWN (En panne)
- **Description** : Le véhicule est hors service en attente de réparation
- **Transitions possibles** :
  - `BROKEN_DOWN` → `AVAILABLE` (réparation terminée)
- **Impacts** :
  - ⚠️ Tous les contrats PENDING associés sont **automatiquement annulés**
  - Les événements de panne sont publiés
- **Actions possibles** :
  - POST `/vehicles/{id}/repair` → Marquer comme réparé
  - Récupérer les contrats annulés avec GET `/contracts?status=CANCELLED`

---

## 📋 Statuts des Contrats

### PENDING (En attente)
- **Description** : Le contrat vient d'être créé mais n'a pas commencé
- **Date** : `now < startDate`
- **Transitions possibles** :
  - `PENDING` → `ONGOING` (approbation du contrat)
  - `PENDING` → `CANCELLED` (annulation manuelle ou panne du véhicule)
- **Actions possibles** :
  - POST `/contracts/{id}/approve` → Approuver et passer à ONGOING
  - POST `/contracts/{id}/cancel` → Annuler le contrat
  - PUT `/contracts/{id}` → Modifier les dates

---

### ONGOING (En cours)
- **Description** : Le contrat est actif, la location a commencé
- **Date** : `startDate <= now <= endDate`
- **Transitions possibles** :
  - `ONGOING` → `COMPLETED` (terminaison normale)
  - `ONGOING` → `OVERDUE` (retard détecté par le scheduler)
  - `ONGOING` → `CANCELLED` (annulation manuelle)
- **Véhicule** : Le statut du véhicule passe à `RENTED`
- **Actions possibles** :
  - POST `/contracts/{id}/complete` → Terminer la location
  - POST `/contracts/{id}/cancel` → Annuler la location
  - Le scheduler met à jour quotidiennement à minuit

---

### COMPLETED (Terminé)
- **Description** : Le contrat s'est terminé normalement
- **Date** : `endDate < now` et status = COMPLETED
- **Transitions possibles** : ❌ AUCUNE (état final)
- **Véhicule** : Le statut du véhicule redevient `AVAILABLE`
- **Actions possibles** :
  - GET `/contracts/{id}` → Consulter l'historique
  - DELETE `/contracts/{id}` → Supprimer l'enregistrement

---

### OVERDUE (En retard)
- **Description** : Le contrat a dépassé sa date de fin sans être terminé
- **Date** : `endDate < now` et status = ONGOING/PENDING
- **Condition** : Détecté par le scheduler à minuit quotidiennement
- **Transitions possibles** :
  - `OVERDUE` → `CANCELLED` (si en conflit avec un PENDING du même véhicule)
- **Actions** :
  - 📬 Événement `ContractOverdueEvent` publié
  - 📝 Handler calcule les jours/heures de retard
  - 🔍 Détection des impacts sur les contrats suivants
  - 💾 Logs détaillés avec le délai exact
- **Actions possibles** :
  - POST `/contracts/{id}/complete` → Forcer la termination
  - POST `/contracts/{id}/cancel` → Annuler le contrat
  - GET `/contracts?status=OVERDUE` → Lister les retards

---

### CANCELLED (Annulé)
- **Description** : Le contrat a été annulé
- **Raisons possibles** :
  - Annulation manuelle
  - Panne du véhicule (si PENDING)
  - Conflit avec un autre contrat en retard
- **Transitions possibles** : ❌ AUCUNE (état final)
- **Actions possibles** :
  - GET `/contracts/{id}` → Consulter l'historique
  - DELETE `/contracts/{id}` → Supprimer l'enregistrement

---

## 🔄 Diagramme de transition des contrats

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ÉTATS DES CONTRATS                              │
└─────────────────────────────────────────────────────────────────────┘

                          Création
                            │
                            ▼
                    ┌──────────────┐
                    │   PENDING    │
                    │              │
                    │ startDate > now
                    └──────────────┘
                       │         │
        Approuver       │         │  Annuler ou Panne
        POST/approve    │         │
                        ▼         ▼
                  ┌──────────┐  ┌────────────┐
                  │ ONGOING  │  │ CANCELLED  │
                  │          │  │            │
                  │startDate │  │(État Final)│
                  │≤ now ≤   │  │            │
                  │endDate   │  └────────────┘
                  └──────────┘
                    │         │
      Terminer      │         │  Retard (Scheduler)
      POST/         │         │  ou Annuler
      complete      │         │
                    ▼         ▼
              ┌──────────┐  ┌────────────┐
              │COMPLETED │  │  OVERDUE   │
              │          │  │            │
              │(État)    │  │ endDate<now│
              │Final     │  └────────────┘
              └──────────┘      │
                                │  Annuler ou Terminer
                                │
                                ▼
                            ┌────────────┐
                            │ CANCELLED  │
                            │            │
                            │(État Final)│
                            └────────────┘
```

---

## 🚙 Diagramme de transition des véhicules

```
┌─────────────────────────────────────────────────────────────────────┐
│                  ÉTATS DES VÉHICULES                               │
└─────────────────────────────────────────────────────────────────────┘

              Création
                │
                ▼
        ┌──────────────┐
        │  AVAILABLE   │
        │              │
        │  (Défaut)    │
        └──────────────┘
           │         │
    Louer  │         │  Panne
    contrat│         │  POST/
    ONGOING│         │  breakdown
           ▼         ▼
      ┌────────┐  ┌──────────────┐
      │ RENTED │  │ BROKEN_DOWN  │
      │        │  │              │
      │1 contrat  │Hors service  │
      │ONGOING │  │              │
      └────────┘  └──────────────┘
           │             │
    Fin du │             │  Réparation
    contrat│             │  POST/repair
           │             │
           └─────┬───────┘
                 │
                 ▼
        ┌──────────────┐
        │  AVAILABLE   │
        └──────────────┘
```

---

## 🔍 Validations Métier

### À la création d'un client
- ✅ `firstName` et `lastName` ne sont pas vides
- ✅ `dateOfBirth` est une date valide
- ✅ `licenseNumber` est unique dans la base de données
- ✅ Âge du client >= 18 ans
- ✅ `email` a un format valide (optionnel)

### À la création d'un véhicule
- ✅ `registrationPlate` est unique dans la base de données
- ✅ `brand`, `model`, `motorization`, `color` ne sont pas vides
- ✅ `acquisitionDate` est une date valide et dans le passé

### À la création d'un contrat
- ✅ Client existe
- ✅ Véhicule existe et n'est pas en panne
- ✅ `startDate` < `endDate`
- ✅ `startDate` et `endDate` sont dans le futur
- ✅ Aucun autre contrat ONGOING/PENDING ne chevauche ces dates pour ce véhicule
- ✅ Le contrat n'entre pas en conflit avec d'autres contrats

### À la modification d'un contrat
- ✅ Toutes les validations de création s'appliquent
- ✅ On ne peut pas modifier un contrat COMPLETED ou CANCELLED

---

## ⏰ Scheduler quotidien (à minuit)

### Étape 1 : Détection des retards
```sql
SELECT * FROM contracts 
WHERE status = 'ONGOING' AND endDate < NOW()
```
**Action** : Mettre à jour le statut à `OVERDUE` et publier `ContractOverdueEvent`

**Logs** :
```
⚠️ CONTRAT EN RETARD - ID: 1 | Client: 5 | Véhicule: 3 | Retard: 2 jours 5 heures
```

---

### Étape 2 : Détection des conflits
```sql
SELECT DISTINCT o FROM ContractEntity o 
INNER JOIN ContractEntity p ON o.vehicle.id = p.vehicle.id 
WHERE o.status = 'OVERDUE' 
  AND p.status = 'PENDING' 
  AND o.endDate > p.startDate
```
**Action** : Annuler le contrat OVERDUE qui bloque le PENDING

**Logs** :
```
Annulation du contrat 1 en retard qui empêche des contrats PENDING de démarrer
```

---

## 📬 Événements Publiés

### VehicleBreakdownEvent
- **Déclenché** : `VehicleService.markAsBrokenDown(vehicleId)`
- **Handler** : `VehicleBreakdownEventHandler`
- **Actions** :
  - Récupère tous les contrats PENDING pour ce véhicule
  - Annule chaque contrat
  - Log le nombre de contrats annulés

### ContractOverdueEvent
- **Déclenché** : 
  - `ContractOverdueScheduler.updateContractsInRetard()` (pour ONGOING → OVERDUE)
  - `ContractOverdueScheduler.cancelConflictingContracts()` (pour OVERDUE → CANCELLED)
- **Handler** : `ContractOverdueEventHandler`
- **Actions** :
  - Calcule le délai en jours et heures
  - Détecte les contrats PENDING bloqués
  - Log les informations de retard
  - Identifie les impacts sur les autres contrats

---

## 🔐 Sécurité et Intégrité

### Transactions
- Toutes les opérations CRUD sont transactionnelles
- Le scheduler s'exécute dans une transaction pour assurer la cohérence

### Unicité
- **Plaque d'immatriculation** : Unique par véhicule
- **Numéro de permis** : Unique par client
- **Email** : Unique par client (si fourni)

### Contraintes referentielles
- Un contrat ne peut pas exister sans client valide
- Un contrat ne peut pas exister sans véhicule valide
- Suppression d'un client annule ses contrats en attente

---

## 📊 Requêtes SQL Optimisées

Toutes les opérations de filtrage utilisent des requêtes SQL optimisées avec :
- **Index** sur `status`, `vehicleId`, `clientId`, `endDate`
- **DISTINCT** pour éviter les doublons
- **INNER JOIN** pour les relations
- **WHERE** conditions dans la base de données (pas en application)

Cela garantit les meilleures performances même avec :
- Millions de contrats
- Centaines de véhicules
- Thousands de clients
