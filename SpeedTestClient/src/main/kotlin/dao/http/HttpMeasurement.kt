package dao.http

import Location
import Measurment
import User
import dao.MeasurementCrud
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.json.JSONArray
import org.json.JSONObject
import speedTest.Type
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HttpMeasurement {
    val client = OkHttpClient()
    var ip = ""
    init {
        val jsonString = File("src/main/kotlin/conf/ipconfig.json").readText()

        val jsonObject = JSONObject(jsonString)
        ip = jsonObject.getString("url")

    }
    fun getByUser(user: User): List<Measurment> {
        val url = "${ip}/measurements/user/${user.id}"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Failed to execute request: ${response.code}")
                return emptyList()
            }

            val responseBody = response.body?.string()
            responseBody?.let {
                val jsonArray = JSONArray(it)
                val measurements = mutableListOf<Measurment>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    measurements.add(parseMeasurement(jsonObject))
                }

                return measurements
            }

            return emptyList()
        }

    }

    fun getByTimeFrame(start: LocalDateTime, end: LocalDateTime): List<Measurment> {
        val startTime = start.format(DateTimeFormatter.ISO_DATE_TIME)
        val endTime = end.format(DateTimeFormatter.ISO_DATE_TIME)
        val url = "${ip}/measurements/timeframe/${startTime}/${endTime}"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Failed to execute request: ${response.code}")
                return emptyList()
            }

            val responseBody = response.body?.string()
            responseBody?.let {
                val jsonArray = JSONArray(it)
                val measurements = mutableListOf<Measurment>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    measurements.add(parseMeasurement(jsonObject))
                }

                return measurements
            }

            return emptyList()
        }
    }

    fun insertMany(list: List<Measurment>) : Boolean{



        TODO("Not yet implemented")
    }

    fun getById(id: ObjectId): Measurment? {
        val url = "${ip}/measurements/${id}"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            // Check if the request was successful (HTTP 200 OK)
            if (!response.isSuccessful) {
                // If not, handle the error
                println("Failed to execute request: ${response.code}")
                return null
            }

            // Get the response body as a string
            val responseBody = response.body?.string()

            responseBody?.let {
                val jsonObject = JSONObject(it)
                return parseMeasurement(jsonObject)
            }

            return null
        }

    }

    fun getAll(): List<Measurment>  {
        val url = "${ip}/measurements"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            // Check if the request was successful (HTTP 200 OK)
            if (!response.isSuccessful) {
                // If not, handle the error
                println("Failed to execute request: ${response.code}")
                return emptyList()
            }

            // Get the response body as a string
            val responseBody = response.body?.string()

            responseBody?.let {
                val jsonArray = JSONArray(it)
                val measurements = mutableListOf<Measurment>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    measurements.add(parseMeasurement(jsonObject))
                }

                return measurements
            }

            return emptyList()
        }

    }
    fun insert(obj: Measurment): Boolean {
        println(obj.user)
        val requestBody = JSONObject().apply {
            put("speed", obj.speed)
            put("type", obj.type.name)
            put("provider", obj.provider)
            put("time", obj.time.format(DateTimeFormatter.ISO_DATE_TIME))
            put("location", JSONObject().apply {
                put("type", "Point")
                put("coordinates", obj.location.coordinates)
            })
            obj.user?.let { put("measuredBy", it.id) }
        }

        val request = Request.Builder()
            .url("${ip}/measurements")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    fun update(obj: Measurment): Boolean {
        val url = "${ip}/measurements/${obj.id}"
        val requestBody = JSONObject().apply {
            put("speed", obj.speed)
            put("type", obj.type.name)
            put("provider", obj.provider)
            put("time", obj.time.format(DateTimeFormatter.ISO_DATE_TIME))
            put("location", JSONObject().apply {
                put("type", "Point")
                put("coordinates", obj.location.coordinates)
            })
            obj.user?.let { put("measuredBy", it.id) }
        }
        val request = Request.Builder()
            .url(url)
            .put(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun delete(obj: Measurment): Boolean {
        val url = "${ip}/measurements/${obj.id}"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        try {
            val response = client.newCall(request).execute()
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun parseMeasurement(jsonObject: JSONObject) : Measurment{
        val speed = jsonObject.getLong("speed")
        val type = Type.valueOf(jsonObject.getString("type"))
        val provider = jsonObject.getString("provider")

        val timeString = jsonObject.getString("time")
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val time = LocalDateTime.parse(timeString, formatter)

        val locationObject = jsonObject.getJSONObject("location")
        val location = Location(
            type = locationObject.getString("type"),
            coordinates = locationObject.getJSONArray("coordinates")
                .let { coordinatesArray ->
                    List<Double>(coordinatesArray.length()) { coordinatesArray.getDouble(it) }
                }
        )
        val user = if (!jsonObject.isNull("measuredBy")) {
            parseUser(jsonObject.getJSONObject("measuredBy"))
        } else {
            null
        }
        val id = if (!jsonObject.isNull("_id") && jsonObject.get("_id") is String) {
            ObjectId(jsonObject.getString("_id"))
        } else {
            ObjectId()
        }

        return Measurment(speed, type, provider, location, time, user, id)
    }
    fun parseUser(jsonObject: JSONObject) : User {
        val username = jsonObject.getString("username")
        val password = jsonObject.getString("password")
        val email = jsonObject.getString("email")
        val admin = jsonObject.getBoolean("admin")
        val id = ObjectId(jsonObject.getString("_id"))
        return User(username, password, email, admin, id)
    }

}