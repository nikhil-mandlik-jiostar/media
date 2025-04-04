package androidx.media3.demo.playlist.data.ui.screen.exo_preload_manager

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.demo.playlist.data.data.additionalData
import androidx.media3.demo.playlist.data.data.videos
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager.Status.STAGE_LOADED_FOR_DURATION_MS
import androidx.media3.exoplayer.source.preload.PreloadException
import androidx.media3.exoplayer.source.preload.PreloadManagerListener
import androidx.media3.exoplayer.source.preload.TargetPreloadStatusControl
import androidx.media3.ui.PlayerView
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ExoPreloadManagerDemoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "ExoDemoViewModel"
    }

    private val videoItems = videos.toMutableList()

    private val preloadManager: DefaultPreloadManager
    private val exoplayer: ExoPlayer
    private var currentIndex: Int = 0
    private var firstFrameRendered = false


    private val listener = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            if (!firstFrameRendered) {
                firstFrameRendered = true
                android.os.Trace.endSection()
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


        val targetPreloadStatusControl = object : TargetPreloadStatusControl<Int> {
            override fun getTargetPreloadStatus(rankingData: Int): TargetPreloadStatusControl.PreloadStatus? {
                Log.i(TAG, "getTargetPreloadStatus: for rankingData $rankingData currentIndex ${exoplayer.currentMediaItemIndex}")
                if (abs(rankingData - exoplayer.currentMediaItemIndex) == 2) {
                    return DefaultPreloadManager.Status(STAGE_LOADED_FOR_DURATION_MS, 400L)
                } else if (abs(rankingData - exoplayer.currentMediaItemIndex) == 1) {
                    return DefaultPreloadManager.Status(STAGE_LOADED_FOR_DURATION_MS, 4000L)
                }
                return DefaultPreloadManager.Status(STAGE_LOADED_FOR_DURATION_MS, 4000L)
            }
        }

        val preloadManagerBuilder = DefaultPreloadManager.Builder(
            context,
            targetPreloadStatusControl
        ).setLoadControl(loadController)

        exoplayer = preloadManagerBuilder.buildExoPlayer(
            ExoPlayer.Builder(context)
                .setRenderersFactory(renderFactory)
                .setLoadControl(loadController)
        ).apply {
            playWhenReady = true
            addListener(listener)
        }

        preloadManager = preloadManagerBuilder.build()

        preloadManager.addListener(object : PreloadManagerListener {
            override fun onCompleted(mediaItem: MediaItem) {
                super.onCompleted(mediaItem)
                Log.i(TAG, "onCompleted: $mediaItem")
            }

            override fun onError(exception: PreloadException) {
                super.onError(exception)
                Log.i(TAG, "onError: $exception")
            }
        })

    }

    fun onPlayerViewInitialized(playerView: PlayerView) {
        playerView.player = exoplayer
        videoItems.forEachIndexed { index , url ->
            val mediaItem = MediaItem.fromUri(url)
            preloadManager.add(mediaItem, index)
            val mediaSource = preloadManager.getMediaSource(mediaItem)
            if (mediaSource != null) {
                Log.i(TAG, "onPlayerViewInitialized: mediaSource from preloadManager $mediaSource")
                exoplayer.addMediaSource(mediaSource)
            } else {
                exoplayer.addMediaItem(mediaItem)
            }
        }
        loadItem(0, prepareRequired = true)
    }

    fun loadNext() {
        loadItem(exoplayer.currentMediaItemIndex + 1)
    }

    fun addMediaSource() {
        val mediaItem = MediaItem.fromUri(additionalData.random())
        exoplayer.addMediaItem(mediaItem)
        preloadManager.add(mediaItem, videoItems.size)
        preloadManager.invalidate()
    }

    fun loadItem(index: Int, prepareRequired: Boolean = false) {
        currentIndex = index
        firstFrameRendered = false
        android.os.Trace.beginSection("load:demo:$index")
        preloadManager.setCurrentPlayingIndex(index)
        if (prepareRequired) {
            exoplayer.prepare()
        } else {
            exoplayer.seekTo(index, 0)
        }
        preloadManager.invalidate()
    }

    override fun onCleared() {
        Log.i(TAG, "onCleared: ")
        exoplayer.release()
    }
}