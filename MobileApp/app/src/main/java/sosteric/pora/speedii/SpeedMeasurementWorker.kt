package sosteric.pora.speedii

import Location
import Measurment
import SessionManager
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.google.gson.GsonBuilder
import dao.http.HttpMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sosteric.pora.speedii.localDateTimeGson.LocalDateTimeSerializer
import sosteric.pora.speedtest.IPInfo
import sosteric.pora.speedtest.Type
import speedTest.SpeedTest
import java.time.LocalDateTime

class SpeedMeasurementWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        showNotification("Speed Measurement", "Speedtest started")

        return try {
            val app = applicationContext as SpeediiApplication
            val sessionManager = app.sessionManager
            if(!app.simulateMeasurements) {

                val speedTest = SpeedTest()
                val speed = speedTest.measureCycle { /* Nothing to do here */ }

                Log.d("SpeedMeasurementWorker", "Measured speed: $speed Mbps")

                saveMeasurement(speed, sessionManager)
            } else {
                val speed = (1..100).random().toLong() * 1000000
                saveSimulatedMeasurement(speed, sessionManager)
            }

            Result.success()
        } catch (e: Exception) {
            showNotification("Speed Measurement", "${e.message}")
            Result.retry()
        }
    }

    private suspend fun saveSimulatedMeasurement(speed: Long, sessionManager: SessionManager) {

        withContext(Dispatchers.IO){
            val provider = "Simulated"

            val type = if((1..2).random() == 1) Type.wifi else Type.data

            val location = Location(
                coordinates = getRandomLocationInArea(46.545139401189346, 46.566609197269564,15.614795792873148,15.662003425327057)
            )

            val time = LocalDateTime.now()

            val measurement = Measurment(
                speed = speed,
                type = type,
                provider = provider,
                location = location,
                time = time,
                user = sessionManager.user
            )

            val gson = Gson()
            val measurementJson = gson.toJson(measurement.toAlt())

            val mqttHelper = MqttHelper(applicationContext)
            if (mqttHelper.isConnected()) {
                showMeasurementNotification("Simulated Measurement", "$provider: ${speed / 1000000} Mbps", measurement)
                mqttHelper.publishMessage("measurements/speed", measurementJson)
            } else {
                showMeasurementNotification("Simulated Measurement", "$provider: ${speed / 1000000} Mbps", measurement)
                Thread{
                    HttpMeasurement(sessionManager).insert(measurement)
                }.start()
            }
        }

    }

    fun getRandomLocationInArea(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<Double> {
        val latitude = minLat + (maxLat - minLat) * Math.random()
        val longitude = minLon + (maxLon - minLon) * Math.random()
        return listOf(longitude, latitude)
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
                            showMeasurementNotification("Speed Measurement", "$provider: ${speed / 1000000} Mbps", measurement)
                            mqttHelper.publishMessage("measurements/speed", measurementJson)
                        } else {
                            showMeasurementNotification("Speed Measurement", "$provider: ${speed / 1000000} Mbps",measurement)
                            Thread{
                                HttpMeasurement(sessionManager).insert(measurement)
                            }.start()
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
                Log.d("SpeedMeasurementWorker", "Security exception: ${e.message}")
                callback(null)
            }
        } else {
            Log.d("SpeedMeasurementWorker", "Location permission not granted")
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

    private fun showMeasurementNotification(title: String, message: String, measurement: Measurment) {
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

            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
                .create()

            val measurementJson = gson.toJson(measurement)


            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("openFragment", "MeasurementFragment")
                putExtra("measurement", measurementJson)
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.logo_no_bg)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            with(NotificationManagerCompat.from(applicationContext)) {
                notify(1, notification)
            }
        }
    }
}