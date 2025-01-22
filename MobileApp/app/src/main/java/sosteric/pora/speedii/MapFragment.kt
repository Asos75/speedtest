package sosteric.pora.speedii

import MobileTower
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dao.http.HttpMobileTower
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import sosteric.pora.speedii.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var app: SpeediiApplication

    private var mobileTowers = listOf<MobileTower>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
        val map = binding.mapView
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(46.55757725915696, 15.642884391147039))

        if(!app.sessionManager.isLoggedIn()) binding.button.visibility = View.GONE
        else binding.button.visibility = View.VISIBLE

        binding.button.setOnClickListener {
            val addTowerFragment = AddTowerFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, addTowerFragment)
                .addToBackStack(null)
                .commit()
        }

        lifecycleScope.launch {
            Log.d("MapFragment", "Getting mobile towers")
            mobileTowers = withContext(Dispatchers.IO) {
                HttpMobileTower(app.sessionManager).getAll()
            }

            Log.d("MapFragment", "Mobile towers: $mobileTowers")
            requireActivity().runOnUiThread {
                mobileTowers.forEach {
                    val marker = Marker(map)
                    marker.position = GeoPoint(it.location.coordinates[1], it.location.coordinates[0])
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = "Provider: " + it.provider
                    marker.snippet = "Type: " + it.type + "\n" + "Confirmed: " + it.confirmed
                    map.overlays.add(marker)
                }
                map.invalidate()
            }

        }


        return binding.root
    }

}