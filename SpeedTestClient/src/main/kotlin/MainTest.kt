import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dao.MobileTowerCRUD
import dao.mongodb.MongoMeasurement
import dao.mongodb.MongoMobileTower
import dao.mongodb.MongoUser
import util.*
import kotlin.random.Random
import org.bson.types.ObjectId
import speedTest.Type
import util.GeneratorUtil.generateToMongo

suspend fun main(){
    var conn : MongoDatabase? = DatabaseUtil.setupConnection()
    if (conn != null) {
        DatabaseUtil.listAllCollection(conn).collect{ println(it) }
    }

    //val mobileTower = MobileTower(Location(coordinates = listOf(15.646279, 46.558882)), "telekom", "5G", false, ObjectId("663c79f85f19accb9458926d"))
/*
    val mongoMobileTower = MongoMobileTower(conn)

    val tower = mongoMobileTower.getById(ObjectId("664244c83386fd0aeef7e71d"))

    if (tower != null) {
        tower.provider = "t-2"
        mongoMobileTower.confirm(tower)
    }

    mongoMobileTower.getByConfirmed(true).forEach{ println(it) }

 */

    val mongoMeasurement = MongoMeasurement(conn)
    //mongoMeasurement.getAll().forEach{ println(it) }
    val measurement = mongoMeasurement.getById(ObjectId("66424e01eea14b4e4f80ade6"))
    if (measurement != null) {
        println(measurement)
    }
}
