package dev.mobile.medicalink

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.Document

/**
 * Classe permettant de gérer la connexion à la base de données MongoDB
 */
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

    /**
     * Fonction permettant d'insérer un document dans une collection
     * @param collectionName le nom de la collection
     * @param document le document à insérer
     */
    fun insertDocument(collectionName: String, document: Document) {
        val collection = database.getCollection(collectionName)
        collection.insertOne(document)
    }

    /**
     * Fonction permettant de récupérer tous les documents d'une collection
     * @param collectionName le nom de la collection
     * @return une liste de Document
     */
    fun selectAllDocuments(collectionName: String): List<Document> {
        val collection = database.getCollection(collectionName)
        return collection.find().toList()
    }
}
