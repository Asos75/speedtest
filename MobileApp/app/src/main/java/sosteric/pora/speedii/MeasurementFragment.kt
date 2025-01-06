package sosteric.pora.speedii

import Measurment
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import sosteric.pora.speedii.databinding.FragmentMeasurementBinding
import java.time.format.DateTimeFormatter
import android.app.AlertDialog

// GeoCoder + Marker logic
import android.location.Geocoder
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import java.util.Locale
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.graphics.Typeface
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import androidx.core.content.ContextCompat
// Measurement details
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import org.osmdroid.events.MapListener


class MeasurementFragment : Fragment() {

    private lateinit var binding: FragmentMeasurementBinding
    private lateinit var measurement: Measurment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        measurement = arguments?.getParcelable("measurement")!!
    }

    @SuppressLint("UseCompatLoadingForDrawables", "DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMeasurementBinding.inflate(inflater, container, false)

        // Map
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
        val map = binding.mapView
        map.setMultiTouchControls(true)

        // Point + Marker
        val startPoint = GeoPoint(measurement.location.coordinates[1], measurement.location.coordinates[0])
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // TODO: Add custom marker (Now with a custom one, the zooming doesn't work properly)
        /*val drawable = resources.getDrawable(R.drawable.marker, null) as BitmapDrawable
        val bitmap = Bitmap.createScaledBitmap(drawable.bitmap, 100, 100, false)
        marker.icon = BitmapDrawable(resources, bitmap)*/

        //marker.icon = resources.getDrawable(R.drawable.marker, null)
        //marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.marker)
        //marker.icon.setBounds(0, 0, marker.icon.intrinsicWidth, marker.icon.intrinsicHeight)

        // Add marker to map
        map.overlays.add(marker)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)

        // Ensure marker stays in position when zooming
        /*map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                //map.controller.setCenter(startPoint)
                //marker.position = map.mapCenter as GeoPoint
                //marker.position = startPoint
                return false
            }
        })*/

        // Geocoding
        val speedInMbps = measurement.speed / 1_000_000.0
        val formattedTime = measurement.time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        binding.measurementDetailsTextView.text = getString(R.string.measurement_details, measurement.provider, speedInMbps, formattedTime)
        binding.measurementDetailsTextView.setTextColor(resources.getColor(android.R.color.black, null))

        // Marker click listener
        marker.setOnMarkerClickListener { _, _ ->
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(startPoint.latitude, startPoint.longitude, 1)
            val address = addresses?.get(0)
            val country = address?.countryName
            val city = address?.locality ?: address?.subAdminArea

            // Speed with 2 decimal places
            val speedInMbps = String.format("%.2f", measurement.speed / 1_000_000.0)

            val message = SpannableStringBuilder()
                .append("Country: ", StyleSpan(Typeface.BOLD), 0).append("$country\n")
            if (city != null) {
                message.append("City: ", StyleSpan(Typeface.BOLD), 0).append("$city\n")
            }
            message.append("Type: ", StyleSpan(Typeface.BOLD), 0).append("${measurement.type}\n")
                .append("Speed: ", StyleSpan(Typeface.BOLD), 0).append("$speedInMbps Mbps\n")
                .append("Provider: ", StyleSpan(Typeface.BOLD), 0).append(measurement.provider)

            AlertDialog.Builder(requireContext())
                .setTitle("Location Details")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setNeutralButton("Copy") { _, _ ->
                    val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Location Details", message)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }
                .show()
            true
        }

        // Back button
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