package sosteric.pora.speedii

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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


        return binding.root
    }
}