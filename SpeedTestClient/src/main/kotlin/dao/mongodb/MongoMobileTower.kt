package dao.mongodb

import Location
import MobileTower
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dao.MobileTowerCRUD
import org.bson.Document
import org.bson.types.ObjectId

class MongoMobileTower(database: MongoDatabase?) : MobileTowerCRUD {
    private val collection = (database ?: throw RuntimeException("Database not found")).getCollection<Document>("mobiletowers")
    override suspend fun getByConfirmed(status: Boolean): List<MobileTower> {
        val mobileTowers = mutableListOf<MobileTower>()
        val findFlow = collection.find(Filters.eq("confirmed", status))
        findFlow.collect{ document ->
            mobileTowers.add(parseMobileTower(document))
        }
        return mobileTowers
    }

    override suspend fun confirm(obj: MobileTower): Boolean {
        val filter = Filters.eq("_id", obj.id)
        val result = collection.updateOne(filter, Updates.set("confirmed", true))
        return result.wasAcknowledged() && result.modifiedCount > 0
    }

    override suspend fun getById(id: ObjectId): MobileTower? {
        var mobileTower : MobileTower? = null
        val findFlow = collection.find(Filters.eq("_id", id))
        findFlow.collect{ document ->
            mobileTower = parseMobileTower(document)
        }
        return mobileTower
    }

    override suspend fun getAll(): List<MobileTower> {
        val mobileTowers = mutableListOf<MobileTower>()

        val findFlow = collection.find()
        findFlow.collect { document ->
            mobileTowers.add(parseMobileTower(document))
        }
        return mobileTowers
    }

    override suspend fun insert(obj: MobileTower): Boolean {
        val document = Document()
            .append(
                "location", Document()
                    .append("type", obj.location.type)
                    .append("coordinates", obj.location.coordinates)
            )
            .append("operator", obj.provider)
            .append("type", obj.type)
            .append("confirmed", obj.confirmed)
            .append("locator", obj.locator)

        collection.insertOne(document)
        return true
    }

    override suspend fun update(obj: MobileTower): Boolean {
        val filter = Filters.eq("_id", obj.id)


        val updates = Updates.combine(
            Updates.set("location", Document()
                .append("type", obj.location.type)
                .append("coordinates", obj.location.coordinates)
            ),
            Updates.set("operator", obj.provider),
            Updates.set("type", obj.type),
            Updates.set("confirmed", obj.confirmed),
            Updates.set("locator", obj.locator),
        )

        val result = collection.updateOne(filter, updates)
        return result.wasAcknowledged() && result.modifiedCount > 0
    }

    override suspend fun delete(obj: MobileTower): Boolean {
        val filter = Document("_id", obj.id)
        val result = collection.deleteOne(filter)
        return result.wasAcknowledged() && result.deletedCount > 0
    }

    fun parseMobileTower(document: Document): MobileTower {
        val id = document.getObjectId("_id")
        val locationDoc = document.get("location", Document::class.java)
        val coordinates = locationDoc?.get("coordinates", List::class.java) as? List<Number>
        val location = coordinates?.let { Location(coordinates = listOf(it[0].toDouble(), it[1].toDouble())) }
            ?: throw IllegalStateException("Invalid location coordinates")
        val provider = document.getString("operator")
        val type = document.getString("type")
        val confirmed = document.getBoolean("confirmed")
        val locator = document.getObjectId("locator")
        return MobileTower(location, provider, type, confirmed, locator, id)
    }

}