package sosteric.pora.speedii

import Measurment
import PaddingItemDecoration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dao.http.HttpMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sosteric.pora.speedii.databinding.FragmentProfileBinding
import android.graphics.Typeface
import java.time.format.DateTimeFormatter
import android.text.Html

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var app: SpeediiApplication

    private lateinit var measurements: List<Measurment>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        if (!app.sessionManager.isLoggedIn()) {
            // He should not be here but we check for login anyways
            requireActivity().supportFragmentManager.beginTransaction()
                .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, LoginFragment())
                .commit()
        } else {
            lifecycleScope.launch {
                measurements = withContext(Dispatchers.IO) {
                    HttpMeasurement(app.sessionManager).getByUser(app.sessionManager.user!!)
                }
                Log.d("ProfileFragment", "Measurements: $measurements")
                val userString = getString(R.string.user, app.sessionManager.user!!.username)
                val emailString = getString(R.string.email, app.sessionManager.user!!.email)

                binding.usernameTextView.text = userString
                binding.usernameTextView.setTypeface(null, Typeface.BOLD)
                binding.emailTextView.text = emailString
                binding.emailTextView.setTypeface(null, Typeface.BOLD)
            }
        }

        binding.logoutButton.setOnClickListener {
            app.sessionManager.destroy()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, LoginFragment())
                .commit()
        }
        
        val recyclerView = binding.measurementsRecyclerView
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        lifecycleScope.launch {
            measurements = withContext(Dispatchers.IO) {
                HttpMeasurement(app.sessionManager).getByUser(app.sessionManager.user!!)

            }
            recyclerView.adapter = MeasurementAdapter(
                measurements,
                { item -> onItemClick(item) },
                { item -> onItemLongClick(item) }
            )
            recyclerView.addItemDecoration(PaddingItemDecoration(8))
        }

        return binding.root
    }

    private fun onItemClick(pos: Int) {
        val measurement = measurements[pos]
        val fragment = MeasurementFragment.newInstance(measurement)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun onItemLongClick(pos: Int) {
        val measurement = measurements[pos]

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Item")

        builder.setMessage(Html.fromHtml("Are you sure you want to delete the measurement from <b>${measurement.provider}</b> " +
                "with speed <b>${(measurement.speed / 1000000)} Mbps</b> taken on <b>${measurement.time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))}</b>?"))

        builder.setPositiveButton("Yes") { dialog, _ ->
            Log.d("MainActivity", "Attempting to delete: ${measurement}")

            lifecycleScope.launch {
                val success = withContext(Dispatchers.IO) {
                    HttpMeasurement(app.sessionManager).delete(measurement)
                }
                if(success) {
                    lifecycleScope.launch {
                        measurements = withContext(Dispatchers.IO) {
                            HttpMeasurement(app.sessionManager).getByUser(app.sessionManager.user!!)
                        }
                    }
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}