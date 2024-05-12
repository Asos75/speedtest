package dao.mongodb

import User
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.Document

class MongoUser(val database: MongoDatabase?) {

    val collection = (database ?: throw RuntimeException("Database not found")).getCollection<Document>("user ") //TODO fix space

    suspend fun getAll(): List<User>{
        val users = mutableListOf<User>()

        val cursor = collection.find()
        cursor.collect { document ->
            val id = document.getObjectId("_id")
            val username = document.getString("username")
            val password = document.getString("password")

            users.add(User(id, username, password))
        }
        return users
    }


}