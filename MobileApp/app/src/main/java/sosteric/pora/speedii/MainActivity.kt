package sosteric.pora.speedii

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import sosteric.pora.speedii.databinding.ActivityMainBinding
import sosteric.pora.speedii.ui.theme.SpeediiTheme

class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    private lateinit var binding: ActivityMainBinding

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
                        .replace(binding.fragmentContainer.id, TowerActivity())
                        .commit()
                    true
                }
                R.id.navigation_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainer.id, ProfileFragment())
                        .commit()
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