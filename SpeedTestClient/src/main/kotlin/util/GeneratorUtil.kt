package util
import Location
import Measurment
import MobileTower
import SessionManager
import User
import dao.http.HttpMeasurement
import dao.http.HttpMobileTower
import de.articdive.jnoise.core.api.functions.Interpolation
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction
import de.articdive.jnoise.pipeline.JNoise
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

        // Parameters for noise scaling
        val noiseScale = 1.0  // Adjust this to control the frequency of noise
        val speedRange = maxValue - minValue

        // Create the PerlinNoise generator using JNoise builder
        val noisePipeline = JNoise.newBuilder()
            .perlin(123456L, Interpolation.COSINE, FadeFunction.QUINTIC_POLY)  // Perlin noise with cosine interpolation and fade function
            .scale(1.0)  // Set a larger scale to increase variance
            .build()

        repeat(count) { index ->
            // Generate coordinates using smooth noise
            val x = Random.nextDouble()
            val y = Random.nextDouble()

            val lon = lonRange.start + x * (lonRange.endInclusive - lonRange.start)
            val lat = latRange.start + y * (latRange.endInclusive - latRange.start)

            // Use Perlin noise to influence the speed (this will generate a more realistic noise pattern)
            val noiseValue = noisePipeline.evaluateNoise(x * noiseScale, y * noiseScale)  // Generate noise from the pipeline

            // Normalize and manipulate the noise to make it approach min and max values more frequently
            val normalizedNoise = (noiseValue + 1) / 2  // Normalize noise to [0, 1]

            // Introducing more variance
            val speedNoise = normalizedNoise * normalizedNoise  // Apply a square function to make extremes more likely
            val speed = minValue + (speedNoise * speedRange).toLong()  // Apply the noise to the speed range

            // Create measurement
            val location = Location(
                coordinates = listOf(lon, lat)
            )
            val measurement = Measurment(speed, type, operator, location, LocalDateTime.now(), user)
            measurements.add(measurement)

            // Insert in batches of 300
            if (measurements.size == 300) {
                HttpMeasurement(sessionManager).insertMany(measurements)

                measurements.clear()
            }
        }

        // Insert remaining measurements
        if (measurements.isNotEmpty()) {
            HttpMeasurement(sessionManager).insertMany(measurements)
        }
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