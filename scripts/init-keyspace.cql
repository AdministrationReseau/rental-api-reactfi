-- Création du keyspace rental
CREATE KEYSPACE IF NOT EXISTS rental
    WITH REPLICATION = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

-- Utiliser le keyspace
USE rental;

-- Message de confirmation
-- Le keyspace 'rental' a été créé avec succès

-- Les tables seront créées automatiquement par Spring Data Cassandra
-- avec l'option spring.cassandra.schema-action=CREATE_IF_NOT_EXISTS
