package dev.insilicon.moddedDungeons.Playerdata;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

public class MongoDB {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> playerCollection;
    private boolean connected = false;

    public MongoDB() {
        connect();
    }

    public void connect() {
        try {
            FileConfiguration config = ModdedDungeons.instance.getConfig();
            String host = config.getString("mongodb.host");
            int port = config.getInt("mongodb.port");
            String databaseName = config.getString("mongodb.database");
            String username = config.getString("mongodb.username");
            String password = config.getString("mongodb.password");

            ConnectionString connectionString;
            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                connectionString = new ConnectionString("mongodb://" + username + ":" + password + "@" + host + ":" + port);
            } else {
                connectionString = new ConnectionString("mongodb://" + host + ":" + port);
            }

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();

            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(databaseName);
            playerCollection = database.getCollection("players");
            connected = true;

            ModdedDungeons.instance.getLogger().info("Successfully connected to MongoDB!");
        } catch (MongoException e) {
            ModdedDungeons.instance.getLogger().severe("Failed to connect to MongoDB: " + e.getMessage());
            connected = false;
        }
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            connected = false;
            ModdedDungeons.instance.getLogger().info("MongoDB connection closed");
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public MongoCollection<Document> getPlayerCollection() {
        return playerCollection;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
