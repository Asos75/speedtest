package dao.mongodb

import Event
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dao.EventCRUD
import org.bson.Document
import org.bson.types.ObjectId

class MongoEvent(database: MongoDatabase?)  : EventCRUD {
    private val collection = (database ?: throw RuntimeException("Database not found")).getCollection<Document>("events")

    override suspend fun getById(id: ObjectId): Event? {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): List<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(obj: Event): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: Event): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: Event): Boolean {
        TODO("Not yet implemented")
    }
}