package dao.http

import Location
import MobileTower
import SessionManager
import User
import dao.MobileTowerCRUD
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.bson.types.ObjectId
import org.jetbrains.skiko.nativeInitializeAccessible
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.format.DateTimeFormatter

class HttpMobileTower(val sessionManager: SessionManager) : MobileTowerCRUD{
    private val client = OkHttpClient()
    var ip = ""
    init {
        val jsonString = File("src/main/kotlin/conf/ipconfig.json").readText()

        val jsonObject = JSONObject(jsonString)
        ip = jsonObject.getString("url")

    }
    override fun getByConfirmed(status: Boolean): List<MobileTower> {
        val url = "${ip}/mobile/confirmed/${status}"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                // If not, handle the error
                println("Failed to execute request: ${response.code}")
                return emptyList()
            }
            val responseBody = response.body?.string()

            responseBody?.let {
                val jsonArray = JSONArray(it)
                val mobileTowers = mutableListOf<MobileTower>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    mobileTowers.add(parseMobileTower(jsonObject))
                }

                return mobileTowers
            }

            return emptyList()
        }

    }

    override fun toggleConfirm(obj: MobileTower): Boolean {
        val url = "${ip}/mobile/confirm/${obj.id}"
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .get()
            .build()


        client.newCall(request).execute().use { response ->
            if(response.isSuccessful){
                obj.confirmed.not()
                return true
            }
            return false
        }

    }

    override fun getById(id: ObjectId): MobileTower? {
        val url = "${ip}/mobile/$id"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    val jsonObject = JSONObject(it)
                    return parseMobileTower(jsonObject)
                }
            }
            return null
        }
    }

    override fun getAll(): List<MobileTower> {
        val url = "$ip/mobile"
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
                val mobileTowers = mutableListOf<MobileTower>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    mobileTowers.add(parseMobileTower(jsonObject))
                }

                return mobileTowers
            }

            return emptyList()
        }
    }
    override fun insert(obj: MobileTower): Boolean {
        val url = "$ip/mobile"
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
    override fun insertMany(list: List<MobileTower>): Boolean{
        val jsonArray = JSONArray()
        list.forEach { tower ->
            val jsonObject = JSONObject().apply {
                put("location", JSONObject().apply {
                    put("type", "Point")
                    put("coordinates", tower.location.coordinates)
                })
                put("operator", tower.provider)
                put("type", tower.type)
                put("confirmed", tower.confirmed)
                put("locator", tower.locator.toString())
            }
            jsonArray.put(jsonObject)
        }

        val jsonTowers = JSONObject().apply {
            put("towers", jsonArray)
        }

        val requestBody = jsonTowers.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("${ip}/mobileTowerRoutes/createMany")
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    override fun update(obj: MobileTower): Boolean {
        val url = "$ip/mobile/${obj.id}"
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

    override fun delete(obj: MobileTower): Boolean {
        val url = "$ip/mobile/${obj.id}"
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }
    private fun createRequestBody(obj: MobileTower): okhttp3.RequestBody {
        val json = JSONObject().apply {
            put("location", JSONObject().apply {
                put("type", "Point")
                put("coordinates", obj.location.coordinates)
            })
            put("operator", obj.provider)
            put("type", obj.type)
            put("confirmed", obj.confirmed)
            if(obj.locator != null) {
                put("locator", obj.locator!!.id)
            }
        }
        return json.toString().toRequestBody("application/json".toMediaType())
    }

    fun parseMobileTower(jsonObject: JSONObject): MobileTower {
        val locationObject = jsonObject.getJSONObject("location")
        val coordinates = locationObject.getJSONArray("coordinates")
        val location = Location(coordinates = listOf(coordinates.getDouble(0), coordinates.getDouble(1)))
        val operator = jsonObject.getString("operator")
        val type = jsonObject.getString("type")
        val confirmed = jsonObject.getBoolean("confirmed")
        val locator = if (!jsonObject.isNull("locator")) {
            parseUser(jsonObject.getJSONObject("locator"))
        } else {
            null
        }
        val id = if (jsonObject.has("_id")) ObjectId(jsonObject.getString("_id")) else ObjectId()
        return MobileTower(location, operator, type, confirmed, locator, id)
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