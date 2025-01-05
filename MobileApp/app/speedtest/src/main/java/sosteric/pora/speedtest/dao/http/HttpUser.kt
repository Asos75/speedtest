package dao.http

import SessionManager
import User
import dao.UserCRUD
import okhttp3.*
import org.bson.types.ObjectId
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class HttpUser(val sessionManager: SessionManager): UserCRUD{
    val client = OkHttpClient()
    var ip = "http://20.160.43.196:3000"

    override fun authenticate(username: String, password: String) : Boolean {


        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("${ip}/users/login")
            .post(requestBody)
            .build()

        var token: String? = null
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonObject = JSONObject(responseBody)
                token = jsonObject.getString("token")
                println("Login successful: $responseBody")
                if(token == null)
                    return false
                sessionManager.start(Pair(token!!, parseUser(jsonObject.getJSONObject("user"))))
                return true
            } else {
                println("Login failed: ${response.code}")
                return false
            }
        }
    }



    override fun getById(id: ObjectId): User? {
        val url = "${ip}/users/$id"
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                responseBody?.let {
                    val jsonObject = JSONObject(it)
                    return parseUser(jsonObject)
                }
            }
            return null
        }
    }

    override fun getAll(): List<User> {
        val url = "$ip/users"
        println(sessionManager.token)
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Failed to execute request: ${response.code}: ${response.body?.string()}")
                return emptyList()
            }
            val responseBody = response.body?.string()
            println(responseBody)
            val jsonObject = JSONObject(responseBody)
            jsonObject.getJSONArray("users").let {
                val jsonArray = JSONArray(it)
                val users = mutableListOf<User>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    users.add(parseUser(jsonObject))
                }
                return users
            }
            return emptyList()
        }
    }

    override fun insert(obj: User) : Boolean{
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("username", obj.username)
            .add("password", obj.password)
            .add("email", obj.email)
            .add("admin", obj.admin.toString())
            .build()

        val request = Request.Builder()
            .url("${ip}/users/")
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    override fun update(obj: User): Boolean {
        val url = "$ip/users/${obj.id}"
        val requestBody = FormBody.Builder()
            .add("username", obj.username)
            .add("password", obj.password)
            .add("email", obj.email)
            .add("admin", obj.admin.toString())
            .build()
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .put(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    override fun delete(obj: User): Boolean {
        val url = "$ip/users/${obj.id}"
        val request = Request.Builder()
            .url(url)
            .addHeader("authorization", "Bearer ${sessionManager.token}")
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
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