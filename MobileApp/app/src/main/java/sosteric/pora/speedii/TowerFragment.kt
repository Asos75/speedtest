package sosteric.pora.speedii

import MobileTower
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.location.Geocoder
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import sosteric.pora.speedii.databinding.FragmentTowerBinding
import java.util.Locale

class TowerFragment : Fragment() {

    private lateinit var binding: FragmentTowerBinding

    private lateinit var tower: MobileTower

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tower = arguments?.getParcelable("tower")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTowerBinding.inflate(inflater, container, false)

        org.osmdroid.config.Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(
            Context.MODE_PRIVATE))
        val map = binding.mapView
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(tower.location.coordinates[1], tower.location.coordinates[0])
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        map.overlays.add(marker)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)

        binding.typeTextView.text = getString(R.string.provider_info, tower.provider)
        binding.providerTextView.text = getString(R.string.type, tower.type)
        binding.confirmedImageView.setImageResource(if (tower.confirmed) R.drawable.green_checkmark_icon else R.drawable.red_x_icon)

        marker.setOnMarkerClickListener { _, _ ->
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(startPoint.latitude, startPoint.longitude, 1)
            val address = addresses?.get(0)
            val country = address?.countryName
            val city = address?.locality ?: address?.subAdminArea

            val message = SpannableStringBuilder()
                .append("Country: ", StyleSpan(Typeface.BOLD), 0).append("$country\n")
            if (city != null) {
                message.append("City: ", StyleSpan(Typeface.BOLD), 0).append("$city\n")
            }
            message.append("Provider: ", StyleSpan(Typeface.BOLD), 0).append(tower.provider)
                .append("Type: ", StyleSpan(Typeface.BOLD), 0).append("${tower.type}\n")

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

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    companion object {
        fun newInstance(tower: MobileTower): TowerFragment {
            val fragment = TowerFragment()
            val args = Bundle()
            args.putParcelable("tower", tower)
            fragment.arguments = args
            return fragment
        }
    }
}