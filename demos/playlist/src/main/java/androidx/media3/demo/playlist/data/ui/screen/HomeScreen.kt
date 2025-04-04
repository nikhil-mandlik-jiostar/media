package androidx.media3.demo.playlist.data.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.demo.playlist.data.domain.Screen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                navigate(Screen.ExoDemo.route)
            }
        ) {
            Text("Exoplayer Demo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navigate(Screen.ExoPlayListDemo.route)
            }
        ) {
            Text("Exoplayer Playlist Demo")
        }
    }
}