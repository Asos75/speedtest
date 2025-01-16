package sosteric.pora.speedii

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import sosteric.pora.speedii.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var app: SpeediiApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        // Set up the spinner with mode options
        val modeOptions = resources.getStringArray(R.array.mode_options)
        val modeKeys = resources.getStringArray(R.array.mode_keys)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMode.adapter = adapter

        val currentModeKey = app.sharedPreferences.getString("appTheme", "default")
        val modeIndex = modeKeys.indexOf(currentModeKey)
        binding.spinnerMode.setSelection(modeIndex)

        binding.spinnerMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedModeKey = modeKeys[position]
                app.sharedPreferences.edit().putString("appTheme", selectedModeKey).apply()
                app.applyTheme(selectedModeKey)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // TODO - Instantly change language when it's changed (either restart app or use a language change listener)
        val languageOptions = resources.getStringArray(R.array.language_options)
        val languageKeys = resources.getStringArray(R.array.language_keys)
        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languageOptions)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = languageAdapter

        val currentLanguageKey = app.sharedPreferences.getString("language", "default")
        val languageIndex = languageKeys.indexOf(currentLanguageKey)
        binding.spinnerLanguage.setSelection(languageIndex)

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedLanguageKey = languageKeys[position]
                app.sharedPreferences.edit().putString("language", selectedLanguageKey).apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Do nothing
            }
        }

        if(!app.sessionManager.isLoggedIn()){
            binding.textViewFrequency.visibility = View.INVISIBLE
            binding.seekBar.visibility = View.INVISIBLE
            binding.seekBarValue.visibility = View.INVISIBLE
            binding.textViewBackground.visibility = View.INVISIBLE
            binding.switchBackground.visibility = View.INVISIBLE
            binding.textViewSimulate.visibility = View.INVISIBLE
            binding.switchSimulate.visibility = View.INVISIBLE
        } else {
            if(!app.sessionManager.user!!.admin){
                binding.textViewSimulate.visibility = View.INVISIBLE
                binding.switchSimulate.visibility = View.INVISIBLE
            }
        }


        val currentFrequency = app.sharedPreferences.getInt("frequency", 30)
        val seekBar = binding.seekBar
        seekBar.progress = currentFrequency
        binding.seekBarValue.visibility = View.INVISIBLE
        binding.seekBarValue.text = currentFrequency.toString()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val currentValue = progress
                binding.seekBarValue.text = currentValue.toString()
                Log.d("SettingsFragment", "Frequency: $currentValue")
                app.sharedPreferences.edit().putInt("frequency", currentValue).apply()
                // Update the position of the TextView
                val thumbPos = seekBar.thumb.bounds.centerX()
                binding.seekBarValue.x = seekBar.x + thumbPos - binding.seekBarValue.width / 2
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                binding.seekBarValue.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                binding.seekBarValue.visibility = View.INVISIBLE
            }
        })

        val backgroundMeasurements = app.sharedPreferences.getBoolean("backgroundMeasurements", false)
        val backgroundMeasurementsSwitch = binding.switchBackground
        backgroundMeasurementsSwitch.isChecked = backgroundMeasurements
        backgroundMeasurementsSwitch.setOnCheckedChangeListener { _, isChecked ->
            app.sharedPreferences.edit().putBoolean("backgroundMeasurements", isChecked).apply()
        }



        val simulateMeasurements = app.sharedPreferences.getBoolean("simulateMeasurements", false)
        val simulateMeasurementsSwitch = binding.switchSimulate
        simulateMeasurementsSwitch.isChecked = simulateMeasurements
        simulateMeasurementsSwitch.setOnCheckedChangeListener { _, isChecked ->
            app.sharedPreferences.edit().putBoolean("simulateMeasurements", isChecked).apply()
        }

        return binding.root
    }
}