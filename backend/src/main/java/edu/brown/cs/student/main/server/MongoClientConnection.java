package edu.brown.cs.student.main.server;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientConnection {
        public static MongoClient startConnection() throws MongoException {
                // NOTE: set a system variable containing creds for this to work!
                String connectionCreds = System.getenv("CONNECTION_CREDS");
                if (connectionCreds == null) {
                        throw new MongoException("Failed to read connection credentials from file");
                }
                // specify details of connection to MongoDB
                String connectionString = "mongodb+srv://" + connectionCreds
                                + "@cs32.oyxzxfh.mongodb.net/?retryWrites=true&w=majority";
                CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
                CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                pojoCodecRegistry);
                ServerApi serverApi = ServerApi.builder()
                                .version(ServerApiVersion.V1)
                                .build();
                MongoClientSettings settings = MongoClientSettings.builder()
                                .applyConnectionString(new ConnectionString(connectionString))
                                .serverApi(serverApi)
                                .codecRegistry(codecRegistry)
                                .build();
                // create a new client and connect to MongoDB
                MongoClient mongoClient = MongoClients.create(settings);
                return mongoClient;
        }
}