package dev.mobile.medicalink

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.Document

class MongoManager {
    private val mongoClient: MongoClient
    private val database: MongoDatabase

    private val mongoHost = "127.26.82.44"
    private val mongoPort = 27666
    private val databaseName = "test"
    private val username = "root"
    private val password = "s5a04a"

    init {
        val clientSettings = MongoClientSettings.builder()
            .applyToClusterSettings { builder ->
                builder.hosts(listOf(ServerAddress(mongoHost, mongoPort)))
            }
            .credential(
                MongoCredential.createCredential(
                    username,
                    databaseName,
                    password.toCharArray()
                )
            )
            .build()

        mongoClient = MongoClients.create(clientSettings)
        database = mongoClient.getDatabase(databaseName)
    }

    // Ajoutez des méthodes pour effectuer des opérations de base de données, par exemple :
    fun insertDocument(collectionName: String, document: Document) {
        val collection = database.getCollection(collectionName)
        collection.insertOne(document)
    }

    // Selectionner tous les élements d'une collection
    fun selectAllDocuments(collectionName: String): List<Document> {
        val collection = database.getCollection(collectionName)
        return collection.find().toList()
    }

    // Vous pouvez ajouter d'autres méthodes pour effectuer des opérations de base de données comme la récupération, la mise à jour, la suppression, etc.
}
