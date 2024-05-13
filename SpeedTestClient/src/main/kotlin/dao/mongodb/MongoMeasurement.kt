package dao.mongodb

import Location
import Measurment
import User
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dao.MeasurementCrud
import org.bson.Document
import org.bson.types.ObjectId
import speedTest.Type
import java.time.LocalDateTime
import java.time.ZoneId

class MongoMeasurement(database: MongoDatabase?) : MeasurementCrud {
    private val collection = (database ?: throw RuntimeException("Database not found")).getCollection<Document>("measurements")
    override suspend fun getByUser(user: User): List<Measurment> {
        var measurments = mutableListOf<Measurment>()
        val findFlow = collection.find(Filters.eq("user", user.id))
        findFlow.collect{ document ->
            measurments.add(parseMeasurement(document))
        }
        return measurments
    }

    override suspend fun getByTimeFrame(start: LocalDateTime, end: LocalDateTime): List<Measurment> {
        val measurements = mutableListOf<Measurment>()
        val findFlow = collection.find(
            Filters.and(
                Filters.gte("time", start),
                Filters.lte("time", end)
            )
        )
        findFlow.collect { document ->
            measurements.add(parseMeasurement(document))
        }
        return measurements
    }

    override suspend fun getById(id: ObjectId): Measurment? {
        var measurment : Measurment? = null
        val findFlow = collection.find(Filters.eq("_id", id))
        findFlow.collect{ document ->
            measurment = parseMeasurement(document)
        }
        return measurment
    }

    override suspend fun getAll(): List<Measurment> {
        val measurments = mutableListOf<Measurment>()

        val findFlow = collection.find()
        findFlow.collect { document ->
            measurments.add(parseMeasurement(document))
        }
        return measurments
    }

    override suspend fun insert(obj: Measurment): Boolean {
        val document = Document()
            .append("speed", obj.speed)
            .append("type", obj.type)
            .append("provider", obj.provider)
            .append("time", obj.time)
            .append(
                "location", Document()
                    .append("type", obj.location.type)
                    .append("coordinates", obj.location.coordinates)
            )
            .append("user", obj.userId)
        collection.insertOne(document)
        return true
    }

    override suspend fun update(obj: Measurment): Boolean {
        val filter = Filters.eq("_id", obj.id)


        val updates = Updates.combine(
            Updates.set("speed", obj.speed),
            Updates.set("type", obj.type),
            Updates.set("provider", obj.provider),
            Updates.set("time", obj.time),
            Updates.set(
                "location", Document()
                    .append("type", obj.location.type)
                    .append("coordinates", obj.location.coordinates)
            ),
            Updates.set("user", obj.userId)
        )

        val result = collection.updateOne(filter, updates)
        return result.wasAcknowledged() && result.modifiedCount > 0
    }

    override suspend fun delete(obj: Measurment): Boolean {
        val filter = Document("_id", obj.id)
        val result = collection.deleteOne(filter)
        return result.wasAcknowledged() && result.deletedCount > 0
    }
    fun parseMeasurement(document: Document) : Measurment{
        val id = document.getObjectId("_id")
        val speed = when (val speedValue = document["speed"]) {
            is Int -> speedValue.toLong()
            is Long -> speedValue
            else -> throw IllegalStateException("Invalid speed value type")
        }
        val type = Type.valueOf(document.getString("type"))
        val provider = document.getString("provider")
        val time = document.getDate("time").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val locationDoc = document.get("location", Document::class.java)
        val coordinates = locationDoc?.get("coordinates", List::class.java) as? List<Number>
        val location = coordinates?.let { Location(coordinates = listOf(it[0].toDouble(), it[1].toDouble())) }
            ?: throw IllegalStateException("Invalid location coordinates")
        val userId = document.getObjectId("user")
        return Measurment(speed, type, provider, location, time, userId, id)
    }
}