.PHONY: help docker-start docker-stop docker-build docker-logs docker-clean

help: ## Affiche cette aide
	@echo "Commandes Docker disponibles:"
	@echo "  docker-start     - Démarre ScyllaDB + initialisation"
	@echo "  docker-app       - Démarre l'application complète"
	@echo "  docker-stop      - Arrête tous les services"
	@echo "  docker-build     - Reconstruit l'image de l'application"
	@echo "  docker-logs      - Affiche les logs"
	@echo "  docker-clean     - Nettoie tout (conteneurs + volumes)"

docker-start: ## Démarre seulement ScyllaDB
	@echo "Démarrage de ScyllaDB..."
	@chmod +x start-docker.sh && ./start-docker.sh

docker-app: ## Démarre l'application complète
	@echo "Démarrage de l'application complète..."
	@docker-compose up -d

docker-stop: ## Arrête tous les services
	@echo "Arrêt des services..."
	@docker-compose down

docker-build: ## Reconstruit l'image de l'application
	@echo "Construction de l'image..."
	@docker-compose build rental-api

docker-logs: ## Affiche les logs de tous les services
	@docker-compose logs -f

docker-logs-app: ## Affiche les logs de l'application
	@docker-compose logs -f rental-api

docker-logs-db: ## Affiche les logs de ScyllaDB
	@docker-compose logs -f scylladb

docker-clean: ## Nettoie tout (conteneurs + volumes)
	@echo "Nettoyage complet..."
	@docker-compose down -v --remove-orphans
	@docker system prune -f

docker-ps: ## Affiche le statut des conteneurs
	@docker-compose ps

docker-shell-app: ## Ouvre un shell dans l'application
	@docker-compose exec rental-api sh

docker-shell-db: ## Ouvre cqlsh dans ScyllaDB
	@docker-compose exec scylladb cqlsh

# Commandes classiques
start: docker-start
stop: docker-stop
clean: docker-clean

# Commandes Maven classiques
build: ## Compile le projet
	@echo "Compilation du projet..."
	@mvn clean compile

test: ## Lance les tests
	@echo "Exécution des tests..."
	@mvn test

package: ## Package l'application
	@echo "Packaging de l'application..."
	@mvn clean package -DskipTests

run: ## Démarre l'application sans Docker
	@echo "Démarrage de l'application..."
	@mvn spring-boot:run
