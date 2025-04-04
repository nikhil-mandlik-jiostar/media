package androidx.media3.demo.playlist.data.ui.screen.exo_demo

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.demo.playlist.data.data.videos
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.PlayerView
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ExoDemoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "ExoDemoViewModel"
    }

    private val _videoItems = videos.toMutableList()

    private val exoplayer: ExoPlayer
    private var currentIndex: Int = 0
    private var firstFrameRendered = false


    private val listener = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            if (!firstFrameRendered) {
                firstFrameRendered = true
                android.os.Trace.endAsyncSection("load:demo:$currentIndex", currentIndex)
            }
        }
    }

    init {
        Log.i(TAG, "initialized: ")
        exoplayer = ExoPlayer.Builder(context)
            .build()
            .apply {
                playWhenReady = true
                repeatMode = REPEAT_MODE_ONE
                addAnalyticsListener(EventLogger())
                addListener(listener)
            }
    }

    fun onPlayerViewInitialized(playerView: PlayerView) {
        playerView.player = exoplayer
        loadItem(0)
    }

    fun loadNext() {
        if (currentIndex < _videoItems.size - 1) {
            loadItem(currentIndex + 1)
        }
    }

    fun loadItem(i: Int) {
        Log.i(TAG, "loadItem: is called for $i")
        android.os.Trace.beginAsyncSection("load:demo:$i", i)
        currentIndex = i
        firstFrameRendered = false
        val videoItem = _videoItems[i]
        exoplayer.setMediaItem(MediaItem.fromUri(videoItem))
        exoplayer.prepare()
    }

    override fun onCleared() {
        Log.i(TAG, "onCleared: ")
        exoplayer.release()
    }
}