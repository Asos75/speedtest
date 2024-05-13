package util
import Location
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.types.ObjectId
import speedTest.Type
import Measurment
import java.io.File
import java.time.LocalDateTime
import kotlin.random.Random

object GeneratorUtil {
    fun generateToCSV(
        minValue: Long,
        maxValue: Long,
        type: Type,
        operator: String,
        locationMarker1: Location,
        locationMarker2: Location,
        userId: ObjectId,
        count: Int
    ){
        val out =  File("speedData.csv").outputStream()

        val latRange = if (locationMarker1.coordinates[0] < locationMarker2.coordinates[0]) {
            locationMarker1.coordinates[0]..locationMarker2.coordinates[0]
        } else {
            locationMarker2.coordinates[0]..locationMarker1.coordinates[0]
        }

        val lonRange = if (locationMarker1.coordinates[1] < locationMarker2.coordinates[1]) {
            locationMarker1.coordinates[1]..locationMarker2.coordinates[1]
        } else {
            locationMarker2.coordinates[1]..locationMarker1.coordinates[1]
        }

        for(i in 0 .. count){
            val value = Random.nextLong(minValue, maxValue)

            val location = Location(
                coordinates = listOf(
                    Random.nextDouble(lonRange.start, lonRange.endInclusive),
                    Random.nextDouble(latRange.start, latRange.endInclusive)
                )
            )

            val measurement = Measurment(value, type, operator, location, LocalDateTime.now(), userId)
            out.write("$measurement\n".toByteArray())
        }
        out.close()

    }

    fun generateToMongo(
        minValue: Long,
        maxValue: Long,
        type: Type,
        operator: String,
        locationMarker1: Location,
        locationMarker2: Location,
        userId: ObjectId,
        count: Int,
        conn: MongoDatabase?
    ) {
        if (conn == null) throw RuntimeException("Database not connected")

        val latRange = if (locationMarker1.coordinates[0] < locationMarker2.coordinates[0]) {
            locationMarker1.coordinates[0]..locationMarker2.coordinates[0]
        } else {
            locationMarker2.coordinates[0]..locationMarker1.coordinates[0]
        }

        val lonRange = if (locationMarker1.coordinates[1] < locationMarker2.coordinates[1]) {
            locationMarker1.coordinates[1]..locationMarker2.coordinates[1]
        } else {
            locationMarker2.coordinates[1]..locationMarker1.coordinates[1]
        }

        val collection = conn.getCollection<Document>("measurements")
        val documents = mutableListOf<Document>()

        repeat(count) {
            val speed = Random.nextLong(minValue, maxValue)
            val location = Location(
                coordinates = listOf(
                    Random.nextDouble(lonRange.start, lonRange.endInclusive),
                    Random.nextDouble(latRange.start, latRange.endInclusive)
                )
            )
            val measurement = Measurment(speed, type, operator, location, LocalDateTime.now(), userId)
            val document = Document()
                .append("speed", measurement.speed)
                .append("type", measurement.type)
                .append("provider", measurement.provider)
                .append("time", measurement.time)
                .append(
                    "location", Document()
                        .append("type", measurement.location.type)
                        .append("coordinates", measurement.location.coordinates)
                )
                .append("user", measurement.userId)
            documents.add(document)
        }
        runBlocking {collection.insertMany(documents)}
    }

}