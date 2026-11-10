package lt.vilniustech.gsk.mkdf_23_2.povilionis

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.theme.BusinessCardTheme

// AppCompatActivity – kad veiktų kalbos perjungimas per AppCompatDelegate
class MainActivity : AppCompatActivity() {

    private val viewModel: CardViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusinessCardTheme {
                // Ekrano dydis: nuo mažo telefono iki planšetės, vertikaliai ir horizontaliai
                val windowSizeClass = calculateWindowSizeClass(this)
                BusinessCardApp(viewModel = viewModel, windowSizeClass = windowSizeClass)
            }
        }
    }
}
