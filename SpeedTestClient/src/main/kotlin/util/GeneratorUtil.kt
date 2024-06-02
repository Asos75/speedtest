package util
import Location
import Measurment
import MobileTower
import SessionManager
import User
import dao.http.HttpMeasurement
import dao.http.HttpMobileTower
import speedTest.Type
import java.io.File
import java.time.LocalDateTime
import kotlin.random.Random
object GeneratorUtil {

    private fun orderCoordinates(locationMarker1: Location, locationMarker2: Location, index: Int) = if (locationMarker1.coordinates[index] < locationMarker2.coordinates[index]) {
        locationMarker1.coordinates[index]..locationMarker2.coordinates[index]
    } else {
        locationMarker2.coordinates[index]..locationMarker1.coordinates[index]
    }
    fun generateMeasurementsToCSV(
        minValue: Long,
        maxValue: Long,
        type: Type,
        operator: String,
        locationMarker1: Location,
        locationMarker2: Location,
        user: User?,
        count: Int
    ){
        val out =  File("speedData.csv").outputStream()

        val lonRange = orderCoordinates(locationMarker1, locationMarker2, 0)

        val latRange = orderCoordinates(locationMarker1, locationMarker2, 1)

        for(i in 0 .. count){
            val value = Random.nextLong(minValue, maxValue)

            val location = Location(
                coordinates = listOf(
                    Random.nextDouble(lonRange.start, lonRange.endInclusive),
                    Random.nextDouble(latRange.start, latRange.endInclusive)
                )
            )

            val measurement = Measurment(value, type, operator, location, LocalDateTime.now(), user)
            out.write("$measurement\n".toByteArray())
        }
        out.close()

    }

    fun generateMeasurementsToMongo(
        minValue: Long,
        maxValue: Long,
        type: Type,
        operator: String,
        locationMarker1: Location,
        locationMarker2: Location,
        user: User?,
        count: Int,
        sessionManager: SessionManager
    ) {

        val lonRange = orderCoordinates(locationMarker1, locationMarker2, 0)

        val latRange = orderCoordinates(locationMarker1, locationMarker2, 1)

        val measurements = mutableListOf<Measurment>()
        repeat(count) {
            val speed = Random.nextLong(minValue, maxValue)
            val location = Location(
                coordinates = listOf(
                    Random.nextDouble(lonRange.start, lonRange.endInclusive),
                    Random.nextDouble(latRange.start, latRange.endInclusive)
                )
            )
            val measurement = Measurment(speed, type, operator, location, LocalDateTime.now(), user)
            measurements.add(measurement)
        }
        HttpMeasurement(sessionManager).insertMany(measurements)
    }

    fun generateTowersToCSV(
        locationMarker1: Location,
        locationMarker2: Location,
        provider: String,
        type: String,
        confirmed: Boolean,
        locator: User?,
        count: Int
    ){
        val out =  File("towerData.csv").outputStream()
        val lonRange = orderCoordinates(locationMarker1, locationMarker2, 0)

        val latRange = orderCoordinates(locationMarker1, locationMarker2, 1)

        for(i in 0 .. count){

            val location = Location(
                coordinates = listOf(
                    Random.nextDouble(lonRange.start, lonRange.endInclusive),
                    Random.nextDouble(latRange.start, latRange.endInclusive)
                )
            )

            val tower = MobileTower(location, provider, type, confirmed, locator)
            out.write("$tower\n".toByteArray())
        }
        out.close()
    }

    fun generateTowersToMongo(
        locationMarker1: Location,
        locationMarker2: Location,
        provider: String,
        type: String,
        confirmed: Boolean,
        locator: User?,
        count: Int,
        sessionManager: SessionManager
        ){
        val lonRange = orderCoordinates(locationMarker1, locationMarker2, 0)

        val latRange = orderCoordinates(locationMarker1, locationMarker2, 1)

        val towers = mutableListOf<MobileTower>()
        repeat(count) {
            val location = Location(
                coordinates = listOf(
                    Random.nextDouble(lonRange.start, lonRange.endInclusive),
                    Random.nextDouble(latRange.start, latRange.endInclusive)
                )
            )
            val tower = MobileTower(location, provider, type, confirmed, locator)
            towers.add(tower)
        }
        HttpMobileTower(sessionManager).insertMany(towers)
    }
}