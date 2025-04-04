package androidx.media3.demo.playlist.data.ui.screen.exo_playlist_demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView

@Composable
fun ExoPlayListDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: ExoPlaylistDemoViewModel = hiltViewModel<ExoPlaylistDemoViewModel>(),
    goBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerUi(modifier = Modifier.weight(2f), viewModel = viewModel)
        ActionButton(modifier = Modifier.weight(1f), viewModel = viewModel)
    }
    BackHandler(true) {
        goBack()
    }
}

@Composable
fun PlayerUi(modifier: Modifier = Modifier, viewModel: ExoPlaylistDemoViewModel) {
    AndroidView(
        factory = {
            PlayerView(it).apply {
                viewModel.onPlayerViewInitialized(this)
            }
        },
        modifier = modifier
            .fillMaxSize()
    )
}


@Composable
fun ActionButton(modifier: Modifier = Modifier, viewModel: ExoPlaylistDemoViewModel) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.loadNext() }
        ) {
            Text("NEXT")
        }

        Button(
            onClick = { viewModel.addMediaSource() }
        ) {
            Text("Add Media Source")
        }
    }
}