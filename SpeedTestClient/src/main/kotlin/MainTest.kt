import dao.http.HttpEvent
import dao.http.HttpMeasurement
import dao.http.HttpMobileTower
import dao.http.HttpUser
import org.bson.types.ObjectId
import speedTest.Type
import util.GeneratorUtil
import java.time.LocalDateTime

fun main(){

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
    /*
    val mongoMeasurement = MongoMeasurement(conn)
    //mongoMeasurement.getAll().forEach{ println(it) }
    val measurement = mongoMeasurement.getById(ObjectId("66424e01eea14b4e4f80ade6"))
    if (measurement != null) {
        println(measurement)
    }
    */
    val user = User("KotlinTester", "1234", "test@mail.com")
    val httpUser = HttpUser(sessionManager)
    httpUser.authenticate("KotlinTester", "1234")

    /*
    val user2 = httpUser.getById(ObjectId("6640e3a9a4a0557c928d34da"))
    httpUser.getAll().forEach{ println(it) }
    */
    /*
    val httMeasurement = HttpMeasurement()
    val measurement2 = Measurment(
        12345,
        Type.wifi,
        "insert test",
        Location(coordinates = listOf(15.640014, 46.562119)),
        LocalDateTime.now(),
        null
    )
    
    val httpTower = HttpMobileTower()
    httpTower.getAll().forEach{ println(it) }
    */
    val httpEvent = HttpEvent(sessionManager)
    val event = Event("FC Barcelona ANC", "Football", LocalDateTime.now(), false, Location(coordinates = listOf(15.640411, 46.562581)))
    //httpEvent.getAll().forEach{ println(it) }
    //httpEvent.getAll().forEach{ println(it) }
    /*
    val event2 = httpEvent.getById(ObjectId("664387dbd3e028a44e619202"))
    if (event2 != null) {
        event2.name = "FC Barcelona v ANC"
        httpEvent.delete(event2)
    }

    println(event2)
    val tower = httpMobileTower.getById(ObjectId("664244c83386fd0aeef7e71d"))
    if (tower != null) {
        httpMobileTower.toggleConfirm(tower)
    }
    */

    println(sessionManager)
    val httpMobileTower = HttpMobileTower(sessionManager)
    val httpMeasurment = HttpMeasurement(sessionManager)
/*
    if(sessionManager.isSet) {
        val startDateTime = LocalDateTime.of(2024, 5, 15, 18, 0)
        val endDateTime = LocalDateTime.of(2024, 5, 15, 18, 30)

        val measurements = httpMeasurment.getByTimeFrame(startDateTime, endDateTime)
        if (measurements != null) {
            measurements.forEach{ println(it) }
        }
    }
*/
/*
    repeat(6){
        GeneratorUtil.generateMeasurementsToMongo(
            50000,
            100000,
            Type.data,
            "Test provider",
            Location(coordinates = listOf(15.615567, 46.570405)),
            Location(coordinates = listOf(15.683889, 46.526323)),
            null,
            500
        )
    }
*/
    val tower = HttpMobileTower(sessionManager).getById(ObjectId("664244da9e0d9045b6f4fe0d"))
    println("$tower")
    if (tower != null) {
        tower.provider = "telemach"
        HttpMobileTower(sessionManager).update(tower)
    }
}
