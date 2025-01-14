package sosteric.pora.speedii

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sosteric.pora.speedii.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(inflater, container, false)

        val result = arguments?.getDouble("speed_result", 0.0) ?: 0.0
        val provider = arguments?.getString("provider", "Unknown") ?: 0.0

        binding.textViewResult.text = "Speed: ${result} ${provider} Mbps"

        binding.buttonBackToTest.setOnClickListener {
             parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SpeedtestFragment())
                .commit()
        }

        return binding.root
    }
}

