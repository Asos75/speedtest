package sosteric.pora.speedii

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import sosteric.pora.speedii.databinding.FragmentTowerSavedBinding

class TowerSavedFragment : Fragment() {

    private lateinit var binding : FragmentTowerSavedBinding
    private lateinit var app: SpeediiApplication



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTowerSavedBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        val towerConfirmed = arguments?.getBoolean("tower_confirmed") ?: false

        if (towerConfirmed) {
            binding.textViewThankYou.text = getString(R.string.thank_you_success)
            binding.imageViewStatus.setImageResource(R.drawable.green_checkmark_icon)
        } else {
            binding.textViewThankYou.text = getString(R.string.thank_you_failure)
            binding.imageViewStatus.setImageResource(R.drawable.red_x_icon)
        }

        binding.buttonBack.setOnClickListener {
            val fragment = MapFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace((requireActivity() as MainActivity).binding.fragmentContainer.id, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root

    }
}