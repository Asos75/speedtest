package sosteric.pora.speedii

import User
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
import sosteric.pora.speedii.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {

    private lateinit var binding: FragmentCreateBinding
    private lateinit var app: SpeediiApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(inflater, container, false)

        app = requireActivity().application as SpeediiApplication

        binding.createAccountButton.setOnClickListener {
            if (binding.usernameEditText.text.toString().isEmpty() || binding.emailEditText.text.toString().isEmpty() || binding.passwordEditText.text.toString().isEmpty()
                || binding.confirmPasswordEditText.text.toString().isEmpty()
            ) {
                binding.errorTextView.text = "Please fill in all fields"
                return@setOnClickListener
            } else if (binding.passwordEditText.text.toString() != binding.confirmPasswordEditText.text.toString()) {
                binding.errorTextView.text = "Passwords do not match"
                return@setOnClickListener
            } else {
                val newUser = User(
                    username = binding.usernameEditText.text.toString(),
                    email = binding.emailEditText.text.toString(),
                    password = binding.passwordEditText.text.toString()
                )

                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        HttpUser(app.sessionManager).insert(newUser)
                    }

                    if (success) {
                        withContext(Dispatchers.IO) {
                            HttpUser(app.sessionManager).authenticate(newUser.username, newUser.password)
                        }

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
                                (requireActivity() as MainActivity).binding.fragmentContainer.id,
                                ProfileFragment()
                            )
                            .commit()
                    } else {
                        binding.errorTextView.text = "Failed to create account"
                    }
                }
            }
        }

        return binding.root
    }
}