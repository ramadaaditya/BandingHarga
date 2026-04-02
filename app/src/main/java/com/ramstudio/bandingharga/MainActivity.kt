package com.ramstudio.bandingharga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ramstudio.bandingharga.ui.theme.BandingHargaTheme
import com.ramstudio.bandingharga.ui.BandingHargaApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BandingHargaTheme {
                BandingHargaApp()
            }
        }
    }
}
