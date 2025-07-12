#!/bin/bash

echo "🚀 Démarrage du projet Rental API Reactive avec Docker"

# Couleurs pour les logs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERREUR]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[ATTENTION]${NC} $1"
}

# Vérifier Docker
if ! command -v docker &> /dev/null; then
    error "Docker n'est pas installé"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    error "Docker Compose n'est pas installé"
    exit 1
fi

# Créer le dossier scripts s'il n'existe pas
mkdir -p scripts

# Créer le fichier init-keyspace.cql s'il n'existe pas
if [ ! -f scripts/init-keyspace.cql ]; then
    log "Création du script d'initialisation ScyllaDB..."
    cat > scripts/init-keyspace.cql << 'EOF'
-- Création du keyspace rental
CREATE KEYSPACE IF NOT EXISTS rental
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

USE rental;
EOF
fi

# Arrêter les conteneurs existants
log "Arrêt des conteneurs existants..."
docker-compose down

# Démarrer seulement ScyllaDB et l'initialisation
log "🗄️ Démarrage de ScyllaDB..."
docker-compose up -d scylladb scylla-init

# Attendre que l'initialisation soit terminée
log "⏳ Attente de l'initialisation de ScyllaDB..."
docker-compose logs -f scylla-init &
LOGS_PID=$!

# Attendre que le service scylla-init soit terminé
while [ "$(docker-compose ps -q scylla-init)" ]; do
    sleep 2
done

kill $LOGS_PID 2>/dev/null

# Vérifier si l'initialisation a réussi
if [ "$(docker-compose ps scylla-init --format json | jq -r '.[0].State')" = "exited" ]; then
    log "✅ ScyllaDB initialisé avec succès"
else
    error "❌ Échec de l'initialisation de ScyllaDB"
    docker-compose logs scylla-init
    exit 1
fi

# Option pour démarrer l'application
read -p "Voulez-vous démarrer l'application Spring Boot ? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    log "🚀 Démarrage de l'application..."
    docker-compose up -d rental-api

    log "⏳ Attente que l'application soit prête..."
    for i in {1..60}; do
        if curl -s http://localhost:8080/api/v1/health > /dev/null 2>&1; then
            break
        fi
        echo -n "."
        sleep 2
    done
    echo ""

    if curl -s http://localhost:8080/api/v1/health > /dev/null 2>&1; then
        log "✅ Application démarrée avec succès!"
        echo ""
        echo -e "${BLUE} URLs importantes:${NC}"
        echo " Application: http://localhost:8080"
        echo " Swagger UI: http://localhost:8080/swagger-ui.html"
        echo " Health: http://localhost:8080/api/v1/health"
        echo " ScyllaDB: localhost:9042"
        echo ""
        echo -e "${BLUE} Commandes Docker utiles:${NC}"
        echo "docker-compose logs -f rental-api    # Logs de l'app"
        echo "docker-compose logs -f scylladb      # Logs de ScyllaDB"
        echo "docker-compose down                  # Arrêter tout"
        echo "docker-compose ps                    # Statut des services"
    else
        error "❌ L'application n'a pas pu démarrer"
        docker-compose logs rental-api
    fi
else
    log "✅ ScyllaDB prêt. Vous pouvez maintenant démarrer votre application avec:"
    echo "mvn spring-boot:run"
    echo ""
    echo "ou avec Docker:"
    echo "docker-compose up -d rental-api"
fi
