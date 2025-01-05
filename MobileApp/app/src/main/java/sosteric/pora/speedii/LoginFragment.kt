package sosteric.pora.speedii

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dao.http.HttpUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sosteric.pora.speedii.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var app: SpeediiApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            lifecycleScope.launch {
                val isAuthenticated = withContext(Dispatchers.IO) {
                    HttpUser(app.sessionManager).authenticate(username, password)
                }

                if (isAuthenticated) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, ProfileFragment())
                        .commit()
                } else {
                    binding.errorTextView.text = "Login failed"
                }
            }
        }

        return binding.root
    }
}