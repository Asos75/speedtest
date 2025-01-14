package sosteric.pora.speedii

import android.Manifest
import Location
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import sosteric.pora.speedii.databinding.FragmentSpeedtestBinding
import sosteric.pora.speedtest.Type
import speedTest.SpeedTest
import Measurment
import com.github.anastr.speedviewlib.Speedometer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import dao.http.HttpMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import sosteric.pora.speedtest.IPInfo
import java.io.IOException
import java.time.LocalDateTime

class SpeedtestFragment : Fragment() {

    private lateinit var binding: FragmentSpeedtestBinding
    private lateinit var app: SpeediiApplication
  //  val resultFragment = ResultFragment()

    private val result = MutableLiveData<Double>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var speedometer: Speedometer

     var speedResult = 0.1;


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedtestBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        speedometer = binding.speedometer
        speedometer.apply {
            maxSpeed = 100f
            withTremble = false

            ticks = arrayListOf(0f, 0.1f, 0.5f, 1f)
            trembleDegree = 2f

            onPrintTickLabel = { tickPosition, tick ->
                when (tick) {
                    0f -> "0"
                    0.1f -> "10"
                    0.5f -> "50"
                    1f -> "100"
                    else -> ""
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        result.observe(viewLifecycleOwner, {
            binding.textViewMeasure.text = "$it Mbps"
            speedResult = it
        })

        // Add this block to set the text based on login status
        val userStatusText = if (app.sessionManager.isLoggedIn()) {
            "You are logged in as ${app.sessionManager.user!!.username}"
        } else {
            "You are using speedii as guest"
        }
        binding.userStatusTextView.text = userStatusText


        binding.buttonMeasure.setOnClickListener {
            Thread {
                val speedtest = SpeedTest()
                requireActivity().runOnUiThread() {
                    speedometer.speedTo(0f)
                    speedometer.withTremble = true
                }

                val res: Long = speedtest.measureCycle {
                    requireActivity().runOnUiThread {
                        speedometer.speedTo(it.toFloat())
                    }
                }

                requireActivity().runOnUiThread {
                    speedometer.withTremble = false
                    speedometer.speedTo(res.toFloat() / 1000000)
                }

                result.postValue(speedtest.convertToMbps(res))

                // Get 'org' from ipinfo.io
                IPInfo.getOrgFromIpInfo { org ->
                    val provider = org ?: "Unknown"  // Default to "Unknown" if org is null

                    Log.d("SpeedtestFragment", "Provider: $provider")
                    val type: Type = checkNetworkType(requireContext()) ?: return@getOrgFromIpInfo
                    val resultBundle = Bundle().apply {

                        putDouble("speed_result", speedResult)
                        putString("provider:", provider)
                    }

                    // Preklopi na ResultFragment z argumenti
                    val resultFragment = ResultFragment().apply {
                        arguments = resultBundle
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, resultFragment)
                        .commit()

                    getLastLocation { customLocation ->
                        if (customLocation != null) {
                            println("Latitude: ${customLocation.coordinates[1]}, Longitude: ${customLocation.coordinates[0]}")

                            // Create the measurement with provider set to the 'org' value
                            val measurement = Measurment(
                                speed = res,
                                type = type,
                                provider = provider,  // Use the fetched 'org' value as the provider
                                location = customLocation,
                                time = LocalDateTime.now(),
                                user = app.sessionManager.user
                            )

                            // Convert to JSON using Gson
                            val gson = Gson()
                            val measurementJson = gson.toJson(measurement.toAlt())

                            // Publish the measurement using MqttHelper
                            val mqttHelper = MqttHelper(requireContext())
                            if (mqttHelper.isConnected()) {
                                Log.d("SpeedtestFragment", "Publishing measurement to MQTT")
                                mqttHelper.publishMessage("measurements/speed", measurementJson)
                            } else {
                                Log.d("SpeedtestFragment", "Publishing measurement to HTTP")
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        HttpMeasurement(app.sessionManager).insert(measurement)
                                    }
                                }
                            }
                        } else {
                            println("Location is null or failed to retrieve")
                        }
                    }
                }
            }.start()
        }

        return binding.root
    }

    fun checkNetworkType(context: Context): Type? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check for network connectivity
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
            // For older Android versions (pre Marshmallow), use the deprecated method
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation
                    .addOnCompleteListener(requireActivity()) { task ->
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


}
