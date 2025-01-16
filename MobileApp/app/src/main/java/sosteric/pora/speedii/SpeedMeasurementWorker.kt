package sosteric.pora.speedii

import Location
import Measurment
import SessionManager
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dao.http.HttpMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sosteric.pora.speedtest.IPInfo
import sosteric.pora.speedtest.Type
import speedTest.SpeedTest
import java.time.LocalDateTime

class SpeedMeasurementWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return try {
            val app = applicationContext as SpeediiApplication
            val sessionManager = app.sessionManager
            if(!app.simulateMeasurements) {

                val speedTest = SpeedTest()
                val speed = speedTest.measureCycle { /* Nothing to do here */ }

                Log.d("SpeedMeasurementWorker", "Measured speed: $speed Mbps")

                saveMeasurement(speed, sessionManager)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("SpeedMeasurementWorker", "Failed to measure speed", e)
            Result.retry()
        }
    }

    private suspend fun saveMeasurement(speed: Long, sessionManager: SessionManager) {


        withContext(Dispatchers.IO) {

            IPInfo.getOrgFromIpInfo { org ->
                val provider = org ?: "Unknown"

                val type: Type = checkNetworkType(applicationContext) ?: return@getOrgFromIpInfo

                getLastLocation { customLocation ->
                    if (customLocation != null) {

                        var time = LocalDateTime.now()
                        val measurement = Measurment(
                            speed = speed,
                            type = type,
                            provider = provider,
                            location = customLocation,
                            time = time,
                            user = sessionManager.user
                        )

                        val gson = Gson()
                        val measurementJson = gson.toJson(measurement.toAlt())


                        val mqttHelper = MqttHelper(applicationContext)
                        if (mqttHelper.isConnected()) {
                            showNotification("Speed Measurement", "$provider: ${speed / 1000000} Mbps")
                            mqttHelper.publishMessage("measurements/speed", measurementJson)
                        } else {
                            showNotification("Speed Measurement", "$provider: ${speed / 1000000} Mbps")
                            HttpMeasurement(sessionManager).insert(measurement)
                        }
                    } else {
                        showNotification("Speed Measurement", "Speedtest failed")
                    }
                }
            }
        }
    }

    fun checkNetworkType(context: Context): Type? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork: Network? = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

            if (networkCapabilities != null) {
                return when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Type.wifi
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Type.data
                    else -> null
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    Type.wifi
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    Type.data
                } else {
                    null
                }
            }
        }

        return null
    }

    private fun getLastLocation(callback: (Location?) -> Unit) {
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation
                    .addOnCompleteListener( ) { task ->
                        val location: android.location.Location? = task.result
                        if (location != null) {
                            val customLocation = Location(
                                coordinates = listOf(location.longitude, location.latitude)
                            )
                            callback(customLocation)
                        } else {
                            callback(null)
                        }
                    }
            } catch (e: SecurityException) {
                println("SecurityException: Permission is not granted or other issue occurred: ${e.message}")
                callback(null)
            }
        } else {
            println("Permission not granted")
            callback(null)
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "speed_measurement_channel"
        val channelName = "Speed Measurement Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("SpeedMeasurementWorker", "Notification permission not granted")
                return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Speed Measurement"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo_no_bg)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Show the notification
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, notification)
        }
    }
    }
}