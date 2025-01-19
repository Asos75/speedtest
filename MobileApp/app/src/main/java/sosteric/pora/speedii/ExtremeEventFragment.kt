package sosteric.pora.speedii

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sosteric.pora.speedii.databinding.FragmentExtremeEventBinding

class ExtremeEventFragment : Fragment() {

    private var _binding: FragmentExtremeEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtremeEventBinding.inflate(inflater, container, false)
        val view = binding.root

        // Nastavi klik za gumb nazaj
        binding.buttonBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
