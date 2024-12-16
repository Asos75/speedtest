package sosteric.pora.speedii

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sosteric.pora.speedii.databinding.FragmentSpeedtestBinding

class SpeedtestFragment : Fragment() {

    private lateinit var binding: FragmentSpeedtestBinding
    private lateinit var app: SpeediiApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedtestBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        return binding.root
    }
}