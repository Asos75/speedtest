package sosteric.pora.speedii

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sosteric.pora.speedii.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var app: SpeediiApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        if(!app.sessionManager.isLoggedIn()) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, LoginFragment())
                .commit()
        } else {
            val userString = getString(R.string.user, app.sessionManager.user!!.username)
            val emailString = getString(R.string.email, app.sessionManager.user!!.email)

            binding.usernameTextView.text = userString
            binding.emailTextView.text = emailString
        }



        return binding.root
    }
}