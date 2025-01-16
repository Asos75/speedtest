package sosteric.pora.speedii

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sosteric.pora.speedii.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    lateinit var binding: ActivityMainBinding

    private lateinit var app: SpeediiApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        app = application as SpeediiApplication

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, SpeedtestFragment())
            .commit()

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_travel -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, SpeedtestFragment())
                        .commit()
                    true
                }
                R.id.navigation_tower ->{
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, MapFragment())
                        .commit()
                    true
                }
                R.id.navigation_profile -> {
                    if(app.sessionManager.isLoggedIn()) {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.fragmentContainer.id, ProfileFragment())
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(binding.fragmentContainer.id, LoginFragment())
                            .commit()
                    }
                    true
                }
                R.id.navigation_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val app = newBase.applicationContext as SpeediiApplication
        val languageContext: Context = app.setLanguage(newBase)
        super.attachBaseContext(languageContext)
    }
}