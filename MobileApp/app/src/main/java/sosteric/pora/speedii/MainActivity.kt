package sosteric.pora.speedii

import Measurment
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import dao.http.HttpMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import sosteric.pora.speedii.databinding.ActivityMainBinding
import android.Manifest
import com.google.gson.GsonBuilder
import sosteric.pora.speedii.localDateTimeGson.LocalDateTimeDeserializer
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    lateinit var binding: ActivityMainBinding
    private lateinit var app: SpeediiApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        app = application as SpeediiApplication

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request Permissions
        checkPermissions()

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, SpeedtestFragment())
            .commit()


        if (intent?.getStringExtra("openFragment") == "MeasurementFragment") {
            val measurementJson = intent.getStringExtra("measurement")

            Log.d("MainActivity", "Measurement: $measurementJson")

            if (measurementJson != null) {
                val gson = GsonBuilder()
                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
                    .create()

                val measurement = gson.fromJson(measurementJson, Measurment::class.java)

                Log.d("MainActivity", "Measurement: $measurement")

                val fragment = MeasurementFragment.newInstance(measurement)
                supportFragmentManager.beginTransaction()
                    .replace(binding.fragmentContainer.id, fragment)
                    .addToBackStack(null)
                    .commit()

            }
        }

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_travel -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, SpeedtestFragment())
                        .commit()
                    true
                }
                R.id.navigation_tower -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, MapFragment())
                        .commit()
                    true
                }
                R.id.navigation_profile -> {
                    if (app.sessionManager.isLoggedIn()) {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.fragmentContainer.id, ProfileFragment())
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.fragmentContainer.id, LoginFragment())
                            .commit()
                    }
                    true
                }
                R.id.navigation_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun checkPermissions() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val backgroundLocationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        // If any permissions are missing, request them
        val permissionsToRequest = mutableListOf<String>()

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (backgroundLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Handle location permission result
            if (grantResults.isNotEmpty()) {
                val locationGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val backgroundLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
                val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }

                if (locationGranted && backgroundLocationGranted && notificationGranted) {
                    Log.d("Permissions", "Permissions granted")
                } else {
                    Log.d("Permissions", "Permissions denied")
                }
            }
        }
    }


    override fun attachBaseContext(newBase: Context) {
        val app = newBase.applicationContext as SpeediiApplication
        val languageContext: Context = app.setLanguage(newBase)
        super.attachBaseContext(languageContext)
    }
}
