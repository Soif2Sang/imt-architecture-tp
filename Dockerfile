# Dockerfile pour PostgreSQL 15
# Image officielle PostgreSQL

FROM postgres:15-alpine

# Variables d'environnement
ENV POSTGRES_USER=tp_user
ENV POSTGRES_PASSWORD=tp_password123
ENV POSTGRES_DB=tp_db

# Créer un répertoire pour les scripts d'initialisation
WORKDIR /docker-entrypoint-initdb.d

# Exposer le port PostgreSQL
EXPOSE 5432

# PostgreSQL démarre par défaut avec ENTRYPOINT