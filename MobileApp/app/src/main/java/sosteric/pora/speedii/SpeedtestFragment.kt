package sosteric.pora.speedii

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import sosteric.pora.speedii.databinding.FragmentSpeedtestBinding
import speedTest.SpeedTest

class SpeedtestFragment : Fragment() {

    private lateinit var binding: FragmentSpeedtestBinding
    private lateinit var app: SpeediiApplication

    private val result = MutableLiveData<Long>()


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedtestBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        result.observe(viewLifecycleOwner, {
            binding.textViewMeasure.text = "$it bps"
        })

        binding.buttonMeasure.setOnClickListener {
            Thread(Runnable {
                val speedtest = SpeedTest()
                val res = speedtest.measure()
                result.postValue(res)
            }).start()
        }

        return binding.root
    }
}