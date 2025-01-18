package sosteric.pora.speedii

import Location
import MobileTower
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dao.http.HttpMobileTower
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import sosteric.pora.speedii.databinding.FragmentAddTowerBinding
import java.io.File

class AddTowerFragment : Fragment() {

    private lateinit var binding : FragmentAddTowerBinding
    private lateinit var app: SpeediiApplication

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var photoUri: Uri? = null
    private var capturedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTowerBinding.inflate(inflater, container, false)


        app = requireActivity().application as SpeediiApplication

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        // Request camera permission
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            100
        )

        // Set up take picture button
        binding.takePictureButton.setOnClickListener { openCamera() }

        // Set up save button
        binding.saveButton.setOnClickListener { saveTower() }

        return binding.root
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == -1) { // RESULT_OK = -1
            capturedImageBitmap = data?.extras?.get("data") as? Bitmap
            binding.imagePreview.setImageBitmap(capturedImageBitmap)
            Toast.makeText(context, "Picture captured", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTower() {
        // Example location (use a proper location provider in production)

        val provider = binding.providerEditText.text.toString()
        val type = binding.typeEditText.text.toString()

        if (provider.isEmpty() || type.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        getLastLocation { location: Location? ->

            if(location == null) {
                Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                return@getLastLocation
            }

            val tower = MobileTower(
                location = location,
                provider = provider,
                type = type,
                confirmed = false, // Always set to false by default
                locator = app.sessionManager.user,
                id = ObjectId()
            )

            capturedImageBitmap?.let { bitmap ->

                Log.d("AddTower", "Trying to save tower")
                lifecycleScope.launch {
                    val result = withContext(Dispatchers.IO){
                        HttpMobileTower(app.sessionManager).confirm(bitmap)
                    }

                    if (result == 1) {
                        Toast.makeText(context, "Tower confirmed successfully.", Toast.LENGTH_SHORT).show()
                        Log.d("AddTower", "Tower confirmed successfully.")
                        tower.confirmed = true
                        val addResult = withContext(Dispatchers.IO){
                            HttpMobileTower(app.sessionManager).insert(tower)
                        }
                        Log.d("AddTower", "Save tower result: $addResult")
                    } else if (result == 0) {
                        Toast.makeText(context, "Failed to confirm tower.", Toast.LENGTH_SHORT).show()
                        Log.d("AddTower", "Failed to confirm tower.")
                        val addResult = withContext(Dispatchers.IO){
                            HttpMobileTower(app.sessionManager).insert(tower)
                        }
                        Log.d("AddTower", "Save tower result: $addResult")
                    } else {
                        Toast.makeText(context, "Failed to confirm tower. Server error.", Toast.LENGTH_SHORT).show()
                        Log.d("AddTower", "Failed to confirm tower. Server error.")
                    }

                    val nextFragment = TowerSavedFragment()
                    val bundle = Bundle()
                    bundle.putBoolean("tower_confirmed", tower.confirmed)
                    nextFragment.arguments = bundle

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
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
