package androidx.media3.demo.playlist.data.ui.screen.exo_playlist_demo

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.demo.playlist.data.data.additionalData
import androidx.media3.demo.playlist.data.data.videos
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.PlayerView
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExoPlaylistDemoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "ExoDemoViewModel"
    }

    private val videoItems = videos.toMutableList()

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

        //load controller
        val loadController = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                8_000, //min buffer
                8_000, //max buffer
                2_000, //buffer for playback start
                4_000 //buffer for playback after re-buffer
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        //Render Factory
        val renderFactory = DefaultRenderersFactory(context)
            .experimentalSetEnableMediaCodecVideoRendererPrewarming(true) //enabling renderer pre-warming

        exoplayer = ExoPlayer.Builder(context)
            .setRenderersFactory(renderFactory)
            .setLoadControl(loadController)
            .setUseLazyPreparation(false) //use this property to enable lazy preparation of next media item
            .build()
            .apply {
                playWhenReady = true
                addAnalyticsListener(EventLogger())
                addListener(listener)
            }
    }

    fun onPlayerViewInitialized(playerView: PlayerView) {
        playerView.player = exoplayer
        videoItems.forEach { exoplayer.addMediaItem(MediaItem.fromUri(it)) }
        loadItem(0, prepareRequired = true)
    }

    fun loadNext() {
        loadItem(exoplayer.currentMediaItemIndex + 1)
    }

    fun addMediaSource() {
        val mediaItem = MediaItem.fromUri(additionalData.random())
        exoplayer.addMediaItem(mediaItem)
    }

    fun loadItem(index: Int, prepareRequired: Boolean = false) {
        android.os.Trace.beginAsyncSection("load:demo:$index", index)
        currentIndex = index
        firstFrameRendered = false
        if (prepareRequired) {
            exoplayer.prepare()
        } else {
            exoplayer.seekTo(index, 0)
        }
    }

    override fun onCleared() {
        Log.i(TAG, "onCleared: ")
        exoplayer.release()
    }
}