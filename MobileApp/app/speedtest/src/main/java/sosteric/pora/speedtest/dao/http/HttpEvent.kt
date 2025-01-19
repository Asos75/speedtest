package dao.http

import Event
import Location
import SessionManager
import dao.EventCRUD
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HttpEvent(val sessionManager: SessionManager): EventCRUD{
    var ip = "http://20.160.43.196:3000"

    val client = OkHttpClient()
     override fun getById(id: ObjectId): Event? {
         val url = "${ip}/event/$id"
         val request = Request.Builder()
             .url(url)
             .get()
             .build()

         client.newCall(request).execute().use { response ->
             if (response.isSuccessful) {
                 val responseBody = response.body?.string()
                 responseBody?.let {
                     val jsonObject = JSONObject(it)
                     return parseEvent(jsonObject)
                 }
             }
             return null
         }
    }

    override fun getAll(): List<Event> {
        val client = OkHttpClient()
        val url = "${ip}/event"
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
                val events = mutableListOf<Event>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    events.add(parseEvent(jsonObject))
                }

                return events
            }

            return emptyList()
        }
    }

    override fun insert(obj: Event): Boolean {
        val url = "$ip/event"
        val requestBody = createRequestBody(obj)
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }
    override fun update(obj: Event): Boolean {
        val url = "$ip/event/${obj.id}"
        val requestBody = createRequestBody(obj)
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .put(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    override fun delete(obj: Event): Boolean {
        val url = "$ip/event/${obj.id}"
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    private fun createRequestBody(obj: Event) : okhttp3.RequestBody {
        val json = JSONObject().apply {
            put("name", obj.name)
            put("type", obj.type)
            put("time", obj.time.format(DateTimeFormatter.ISO_DATE_TIME))
            put("online", obj.online)
            if(obj.location != null) {
                put("location", JSONObject().apply {
                    put("type", "Point")
                    put("coordinates", obj.location!!.coordinates)
                })
            }
        }
        return json.toString().toRequestBody("application/json".toMediaType())
    }

    private fun parseEvent(jsonObject: JSONObject) : Event{
        val name = jsonObject.getString("name")
        val type = jsonObject.getString("type")

        val timeString = jsonObject.getString("time")
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val time = LocalDateTime.parse(timeString, formatter)

        val online = jsonObject.getBoolean("online")

        val location= if (!jsonObject.isNull("location") && jsonObject.get("location") is JSONObject){
            val locationObject = jsonObject.getJSONObject("location")
            Location(
            type = locationObject.getString("type"),
            coordinates = locationObject.getJSONArray("coordinates")
                .let { coordinatesArray ->
                    List<Double>(coordinatesArray.length()) { coordinatesArray.getDouble(it) }
                }
            )
        } else null
        val id =ObjectId(jsonObject.getString("_id"))

        return Event(name, type, time, online, location, id)

    }

}