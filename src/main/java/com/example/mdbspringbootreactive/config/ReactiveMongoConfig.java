package com.example.mdbspringbootreactive.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.lang.NonNull;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;


/**
 * Configuration class for setting up reactive MongoDB with custom client settings and transaction management.
 * It configures connection properties, database settings, and transaction support for a reactive Spring application.
 */
@Configuration
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration {

    private final MongoProperties mongoProperties;

    /**
     * Constructor for initializing ReactiveMongoConfig with MongoDB properties.
     * @param mongoProperties MongoDB properties, including URI and database name.
     */
    public ReactiveMongoConfig(@NonNull MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    /**
     * Returns the database name to connect to, based on MongoDB properties.
     * @return The configured MongoDB database name.
     */
    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDatabase();
    }

    /**
     * Configures client settings for MongoDB connection, including URI, read concern, and write concern.
     * @param builder MongoClientSettings.Builder to customize MongoDB client settings.
     */
    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(mongoProperties.getUri()))
               .readConcern(ReadConcern.SNAPSHOT)  // Ensures consistency during transactions
               .writeConcern(WriteConcern.MAJORITY); // Ensures data is acknowledged by majority of replica set members
    }

    /**
     * Creates and configures a ReactiveMongoTransactionManager to manage MongoDB transactions.
     * @param dbFactory The ReactiveMongoDatabaseFactory used for MongoDB connections.
     * @return A ReactiveMongoTransactionManager for handling MongoDB transactions.
     */
    @Bean
    public ReactiveMongoTransactionManager transactionManager(ReactiveMongoDatabaseFactory dbFactory) {
        return new ReactiveMongoTransactionManager(dbFactory);
    }
}
