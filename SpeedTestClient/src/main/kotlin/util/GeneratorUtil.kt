package util
import speedTest.Type
import speedTest.Measurment
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
        userId: String,
        count: Int
    ){
        val out =  File("speedData.csv").outputStream()

        val latSmaller: Double
        val latBigger: Double
        if(locationMarker1.lat < locationMarker2.lat){
            latSmaller = locationMarker1.lat
            latBigger = locationMarker2.lat
        } else {
            latSmaller = locationMarker2.lat
            latBigger = locationMarker1.lat
        }

        val lonSmaller: Double
        val lonBigger: Double
        if(locationMarker1.lon < locationMarker2.lon){
            lonSmaller = locationMarker1.lon
            lonBigger = locationMarker2.lon
        } else {
            lonSmaller = locationMarker2.lon
            lonBigger = locationMarker1.lon
        }

        for(i in 0 .. count){
            val value = Random.nextLong(minValue, maxValue)

            val location = Location(Random.nextDouble(latSmaller, latBigger), Random.nextDouble(lonSmaller, lonBigger))

            val measurement = Measurment(value, type, operator, location, LocalDateTime.now(), userId)
            out.write("$measurement\n".toByteArray())
        }
        out.close()

    }

    fun generateToMongo(){
        println("generating to mongo")
    }

}