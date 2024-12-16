package sosteric.pora.speedii

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sosteric.pora.speedii.databinding.FragmentTowerBinding

class TowerActivity : Fragment() {

    private lateinit var binding: FragmentTowerBinding
    private lateinit var app: SpeediiApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTowerBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        return binding.root
    }
}