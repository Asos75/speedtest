package dao.mongodb

import User
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dao.UserCRUD
import org.bson.Document
import org.bson.types.ObjectId
import kotlinx.coroutines.flow.first
import org.mindrot.jbcrypt.BCrypt
import util.HashingUtil

class MongoUser(database: MongoDatabase?): UserCRUD {

    private val collection = (database ?: throw RuntimeException("Database not found")).getCollection<Document>("user ") //TODO fix space

    override suspend fun getAll(): List<User>{
        val users = mutableListOf<User>()

        val findFlow = collection.find()
        findFlow.collect { document ->
            val id = document.getObjectId("_id")
            val username = document.getString("username")
            val password = document.getString("password")

            users.add(User( username, password, id))
        }
        return users
    }

    override suspend fun authenticate(obj: User): Boolean {
        val userDocument = collection.find(eq("username", obj.username)).first() ?: return false
        val hashedPassword = userDocument.getString("password") ?: return false

        return BCrypt.checkpw(obj.password, hashedPassword)
    }

    override suspend fun getById(id: ObjectId): User? {
        var user : User? = null
        val findFlow = collection.find(eq("_id", id))
        findFlow.collect{ document ->
            val id = document.getObjectId("_id")
            val username = document.getString("username")
            val password = document.getString("password")

            user = User(username, password, id)
        }
        return user
    }

    override suspend fun insert(obj: User): Boolean {
        val document = Document("username", obj.username)
            .append("password", HashingUtil.hashPassword(obj.password))

        collection.insertOne(document)
        return true
    }

    override suspend fun update(obj: User): Boolean {
        val filter = eq("_id", obj.id)


        val updates = Updates.combine(
            Updates.set("username", obj.username),
            Updates.set("password", HashingUtil.hashPassword(obj.password))
        )

        val result = collection.updateOne(filter, updates)
        return result.wasAcknowledged() && result.modifiedCount > 0
    }
    override suspend fun delete(obj: User): Boolean {
        val filter = Document("_id", obj.id)
        val result = collection.deleteOne(filter)
        return result.wasAcknowledged() && result.deletedCount > 0
    }

}