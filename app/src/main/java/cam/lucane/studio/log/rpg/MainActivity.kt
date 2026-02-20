package cam.lucane.studio.log.rpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import cam.lucane.studio.log.rpg.ui.navigation.LogRPGNavigation
import cam.lucane.studio.log.rpg.ui.theme.LogRPGTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogRPGTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val systemUiController = rememberSystemUiController()

                    WindowCompat.setDecorFitsSystemWindows(window, false)

                    systemUiController.setStatusBarColor(
                        color = Color.Transparent,
                        darkIcons = true
                    )
                    systemUiController.setNavigationBarColor(
                        color = Color.Transparent,
                        navigationBarContrastEnforced = false,
                        darkIcons = true
                    )

                    LogRPGNavigation(navController = navController)
                }
            }
        }
    }
}
