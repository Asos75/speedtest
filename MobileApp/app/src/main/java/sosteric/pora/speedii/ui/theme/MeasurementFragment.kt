package sosteric.pora.speedii

import Measurment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import sosteric.pora.speedii.databinding.FragmentMeasurementBinding
import java.time.format.DateTimeFormatter

class MeasurementFragment : Fragment() {

    private lateinit var binding: FragmentMeasurementBinding
    private lateinit var measurement: Measurment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        measurement = arguments?.getParcelable("measurement")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeasurementBinding.inflate(inflater, container, false)

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
        val map = binding.mapView
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(measurement.location.coordinates[1], measurement.location.coordinates[0])
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)

        val speedInMbps = measurement.speed / 1_000_000.0
        val formattedTime = measurement.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        binding.measurementDetailsTextView.text = getString(R.string.measurement_details, measurement.provider, speedInMbps, formattedTime)
        binding.measurementDetailsTextView.setTextColor(resources.getColor(android.R.color.black, null))

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    companion object {
        fun newInstance(measurement: Measurment): MeasurementFragment {
            val fragment = MeasurementFragment()
            val args = Bundle()
            args.putParcelable("measurement", measurement)
            fragment.arguments = args
            return fragment
        }
    }
}