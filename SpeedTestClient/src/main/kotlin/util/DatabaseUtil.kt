package util

import ch.qos.logback.classic.Level
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import org.bson.BsonInt64
import org.bson.Document
import org.slf4j.LoggerFactory
import java.util.*

object DatabaseUtil {
    suspend fun setupConnection(
        databaseName: String = "test",
        connectionEnvVariable: String = "MONGODB_URI"
    ): MongoDatabase? {
        val connectString = if (System.getenv(connectionEnvVariable) != null) {
            System.getenv(connectionEnvVariable)
        } else {
            "mongodb+srv://kotlinAdmin:M0F8Yq1njPZpa5f6@speeddb.cqupown.mongodb.net/?retryWrites=true&w=majority&appName=SpeedDB"
        }

        val client = MongoClient.create(connectionString = connectString)
        val database = client.getDatabase(databaseName = databaseName)

        return try {
            // Send a ping to confirm a successful connection
            val command = Document("ping", BsonInt64(1))
            database.runCommand(command)
            println("Pinged your deployment. You successfully connected to MongoDB!")
            database
        } catch (me: MongoException) {
            System.err.println(me)
            null
        }
    }

    fun listAllCollection(database: MongoDatabase): Flow<String> {
        val logger = LoggerFactory.getLogger("org.mongodb.driver")
        (logger as ch.qos.logback.classic.Logger).level = Level.INFO
        return database.listCollectionNames()
    }
}