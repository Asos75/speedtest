package sosteric.pora.speedtest

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

object IPInfo {
    fun getOrgFromIpInfo(callback: (String?) -> Unit) {
        val apiToken = "b72c4d29b4b50c"  // Replace with your actual API token

        val client = OkHttpClient()

        // Include the API token in the URL as a query parameter
        val request = Request.Builder()
            .url("http://ipinfo.io?token=$apiToken")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Log.d("IPInfo", "Response received")

                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val jsonObject = JSONObject(json)
                    val org = jsonObject.optString("org")  // Extract the 'org' field
                    callback(org)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("IPInfo", "Error fetching IP info", e)
                callback(null)
            }
        })
    }

}