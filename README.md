# Gestion d’Emprunts Bibliothèque – Architecture Microservices

Application moderne de gestion de bibliothèque réalisée avec une **architecture microservices** utilisant :

- Spring Boot 3
- Spring Cloud (Eureka + Gateway)
- MySQL (pattern Database-per-Service)
- Apache Kafka (communication asynchrone par événements)
- Docker + Docker Compose (déploiement conteneurisé complet)

## Architecture globale

```
                  ┌────────────────────┐
                  │   API Gateway      │  ← 9999
                  └─────────┬──────────┘
                            │
          ┌─────────────────┼─────────────────┐
          │                 │                 │
   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
   │ User Service│   │ Book Service│   │ Emprunt     │
   │   (8082)    │   │   (8081)    │   │ Service     │  ← Producteur Kafka
   └──────┬──────┘   └──────┬──────┘   └──────┬──────┘
          │                 │                 │
     userdb            bookdb           empruntdb     (Bases MySQL isolées)
          │                 │                 │
          └─────────┬───────┴─────────┬───────┘
                    │                 │
             ┌───────────────┐        │
             │   Kafka       │◄───────┘  ← Topic : emprunt-created
             │ + Zookeeper   │
             └───────┬───────┘
                     │
             ┌───────┴───────┐
             │ Notification  │  ← Consommateur Kafka uniquement
             │   Service     │
             │   (8084)      │
             └───────┬───────┘
                     │
               notificationdb
```

## Aperçu des services

| Service              | Port  | Responsabilité principale                          | Base de données         | Rôle Kafka       |
|----------------------|-------|----------------------------------------------------|-------------------------|------------------|
| Eureka Server        | 8761  | Registre de découverte des services                | —                       | —                |
| API Gateway          | 9999  | Point d'entrée unique & routage dynamique          | —                       | —                |
| User Service         | 8082  | Gestion CRUD des utilisateurs                      | userdb (3307)           | —                |
| Book Service         | 8081  | Catalogue des livres & gestion disponibilité       | bookdb (3308)           | —                |
| Emprunt Service      | 8083  | Gestion des emprunts + publication d'événements    | empruntdb (3309)        | Producteur       |
| Notification Service | 8084  | Notifications asynchrones                          | notificationdb (3310)   | Consommateur     |

## Choix architecturaux principaux

- **Database-per-Service** → Isolation forte des données et indépendance des services
- **Architecture événementielle** → Découplage total entre la création d'emprunt et l'envoi de notifications
- **API Gateway** → Point d'entrée unique pour tous les clients
- **Service Discovery** → Enregistrement dynamique et routage intelligent avec Eureka
- **Déploiement conteneurisé** → 12 conteneurs (6 services Spring + 4 MySQL + Kafka + Zookeeper)

## Démarrage rapide

```bash
# Cloner le projet
git clone https://github.com/AbdelfatahMennoun/gestion-emprunts-microservices.git
cd gestion-emprunts-microservices

# Lancer l'ensemble (12 conteneurs)
docker compose up -d --build

# Vérifier l'état
docker compose ps
```

Ports principaux exposés :
- `8761` → Tableau de bord Eureka
- `9999` → API Gateway (point d'entrée unique)

## Exemple complet de flux métier (création d'emprunt)

1. Appel client → `POST /emprunts/{userId}/{bookId}` vers la Gateway (:9999)
2. La Gateway route vers Emprunt Service (:8083)
3. Emprunt Service :
   - Vérifie l'existence de l'utilisateur
   - Vérifie l'existence et la disponibilité du livre
   - Enregistre l'emprunt dans `empruntdb`
   - Publie un événement `EmpruntCreatedEvent` sur le topic Kafka `emprunt-created`
4. Notification Service consomme l'événement et crée une notification

## Exemples de tests API (via la Gateway)

```bash
# 1. Création d'utilisateurs
curl -X POST http://localhost:9999/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Abdelfatah","email":"abdelfatah@example.ma"}'

curl -X POST http://localhost:9999/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Latifa","email":"latifa@example.ma"}'

# 2. Ajout de livres
curl -X POST http://localhost:9999/books \
  -H "Content-Type: application/json" \
  -d '{"titre":"Le Prince","auteur":"Nicolas Machiavel","isbn":"978-2253006473","disponible":true}'

curl -X POST http://localhost:9999/books \
  -H "Content-Type: application/json" \
  -d '{"titre":"La Ferme des animaux","auteur":"George Orwell","isbn":"978-2070416783","disponible":true}'

# 3. Création d'un emprunt (exemple : userId=1, bookId=1)
curl -X POST http://localhost:9999/emprunts/1/1

# 4. Vérification des notifications générées
docker exec -it mysql-notification mysql -uroot -proot notificationdb \
  -e "SELECT * FROM notifications ORDER BY created_at DESC LIMIT 5;"
```

## Commandes utiles (debug & monitoring)

```bash
# Suivre les logs en temps réel
docker compose logs -f emprunt-service
docker compose logs -f notification-service

# Lister les topics Kafka
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Tableau de bord Eureka
http://localhost:8761
```

## Évolutions envisagées

- Authentification JWT + Spring Security
- Documentation OpenAPI/Swagger centralisée
- Résilience (Resilience4j : Circuit Breaker, Retry, Rate Limiter…)
- Observabilité (Micrometer + Prometheus + Grafana)
- Tracing distribué (Zipkin / Jaeger)
- Tests d'intégration avancés (Testcontainers + Kafka)

Projet réalisé pour illustrer les principes modernes des microservices, de l'architecture événementielle et du déploiement conteneurisé.

Bonne continuation !  
**Abdelfatah Mennoun** – 11 janvier 2026
