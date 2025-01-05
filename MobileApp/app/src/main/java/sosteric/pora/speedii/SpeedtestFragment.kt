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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.time.LocalDateTime


class SpeedtestFragment : Fragment() {

    private lateinit var binding: FragmentSpeedtestBinding
    private lateinit var app: SpeediiApplication

    private val result = MutableLiveData<Long>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedtestBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        result.observe(viewLifecycleOwner, {
            binding.textViewMeasure.text = "$it Mbps"
        })

        binding.buttonMeasure.setOnClickListener {
            Thread {
                val speedtest = SpeedTest()
                val res: Long = speedtest.measureCycle()
                result.postValue(speedtest.convertToMbps(res))

                // Save the measurement
                val type: Type = checkNetworkType(requireContext()) ?: return@Thread

                getLastLocation { customLocation ->
                    if (customLocation != null) {
                        println("Latitude: ${customLocation.coordinates[1]}, Longitude: ${customLocation.coordinates[0]}")

                        // Create the measurement
                        val measurement = Measurment(
                            speed = res,
                            type = type,
                            provider = "Speedii",
                            location = customLocation,
                            time = LocalDateTime.now(),
                            user = null
                        )

                        // Convert to JSON using Gson
                        val gson = Gson()
                        val measurementJson = gson.toJson(measurement)

                        // Publish the measurement using MqttHelper
                        val mqttHelper = MqttHelper(requireContext())
                        mqttHelper.publishMessage("measurements/speed", measurementJson)
                    } else {
                        println("Location is null or failed to retrieve")
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