package com.bikematchpro.bikematch;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnector {
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoDBConnector(String connectionString, String databaseName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(databaseName);
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public void close() {
        mongoClient.close();
    }
}
