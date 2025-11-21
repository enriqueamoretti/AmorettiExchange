package dev.eamoretti.amorettiexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.eamoretti.amorettiexchange.presentation.navigation.AppNavGraph
import dev.eamoretti.amorettiexchange.ui.theme.AmorettiExchangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            AmorettiExchangeTheme(darkTheme = isDarkTheme) {
                AppNavGraph()
            }
        }
    }
}