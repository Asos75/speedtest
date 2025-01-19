package sosteric.pora.speedii

import SessionManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class SpeediiApplication : Application() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var userUUID: UUID
    lateinit var appTheme: String
    lateinit var language: String

    var frequency: Int = 30
    var backgroundMeasurements: Boolean = false
    var simulateMeasurements: Boolean = false

    lateinit var mqttHelper: MqttHelper
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("sosteric.pora.application", MODE_PRIVATE)

        mqttHelper = MqttHelper(this)  // Create an instance of MqttHelper
        mqttHelper.connect()  // Connect to the broker

        sessionManager = SessionManager()

        appTheme = if(sharedPreferences.contains("appTheme")){
            sharedPreferences.getString("appTheme", "default")!!
        } else {
            sharedPreferences.edit().putString("appTheme", "default").apply()
            "default"
        }

        when(appTheme){
            "default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        language = if(sharedPreferences.contains("language")){
            sharedPreferences.getString("language", "en")!!
        } else {
            sharedPreferences.edit().putString("language", "en").apply()
            "en"
        }

        when(language){
            "default" -> {
                // Do nothing
            }
            else -> {
                setLanguage(this)
            }
        }

        if(sharedPreferences.contains("userUUID")){
            userUUID = UUID.fromString(sharedPreferences.getString("userUUID", ""))
        } else {
            userUUID = UUID.randomUUID()
            sharedPreferences.edit().putString("userUUID", userUUID.toString()).apply()
        }

        frequency = if(sharedPreferences.contains("frequency")){
            sharedPreferences.getInt("frequency", 30)
        } else {
            sharedPreferences.edit().putInt("frequency", 30).apply()
            30
        }

        backgroundMeasurements = if(sharedPreferences.contains("backgroundMeasurements")){
            sharedPreferences.getBoolean("backgroundMeasurements", false)
        } else {
            sharedPreferences.edit().putBoolean("backgroundMeasurements", false).apply()
            false
        }

        simulateMeasurements = if(sharedPreferences.contains("simulateMeasurements")){
            sharedPreferences.getBoolean("simulateMeasurements", false)
        } else {
            sharedPreferences.edit().putBoolean("simulateMeasurements", false).apply()
            false
        }
        handleBackgroundMeasurements()
    }

    fun handleBackgroundMeasurements() {
        if(backgroundMeasurements){
            Log.d("SpeediiApplication", "Background measurements enabled for every $frequency minutes")
            scheduleSpeedMeasurementWorker(frequency)
        } else {
            Log.d("SpeediiApplication", "Background measurements disabled")
            WorkManager.getInstance(this).cancelAllWorkByTag("SpeedMeasurementWorker")
        }
    }

    private fun scheduleSpeedMeasurementWorker(frequency: Int) {
        WorkManager.getInstance(this).cancelAllWorkByTag("SpeedMeasurementWorker")

        val periodicWorkRequest = PeriodicWorkRequestBuilder<SpeedMeasurementWorker>(
            frequency.toLong(), TimeUnit.MINUTES
        ).addTag("SpeedMeasurementWorker")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SpeedMeasurementWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }
    fun getLanguagePreference() = sharedPreferences.getString("language", "en")

    fun setLanguage(context: Context): Context {
        val locale = when (getLanguagePreference()) {
            "de" -> Locale.GERMANY
            "sl" -> Locale("sl", "SI")
            else -> Locale.US
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    fun applyTheme(appTheme: String){
        when(appTheme){
            "default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}