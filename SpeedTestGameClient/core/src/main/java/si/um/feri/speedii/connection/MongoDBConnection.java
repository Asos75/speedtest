package si.um.feri.speedii.connection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

//UNUSED CLASS
public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void connect() {
        try {
            String connectionString = "mongodb+srv://david:nice@speeddb.cqupown.mongodb.net/?retryWrites=true&w=majority&appName=SpeedDB";
            mongoClient = MongoClients.create(connectionString);

            // Dostop do baze
            database = mongoClient.getDatabase("test");
            System.out.println("Povezava na MongoDB je uspešna!");
            printCollections();
        } catch (Exception e) {
            System.err.println("Napaka pri povezavi na MongoDB:");

            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Povezava na MongoDB je zaprta.");
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    private static void printCollections() {
        if (database == null) {
            System.err.println("Baza ni povezana!");
            return;
        }

        System.out.println("Seznam zbirk (tabel) v bazi:");
        for (String collectionName : database.listCollectionNames()) {
            System.out.println("- " + collectionName);
        }
    }


}
