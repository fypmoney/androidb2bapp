package com.fypmoney.util.videoplayer

import android.app.Application
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.Surface
import androidx.lifecycle.MutableLiveData
import com.amazonaws.ivs.player.MediaPlayer
import com.amazonaws.ivs.player.MediaType
import com.amazonaws.ivs.player.Player
import com.amazonaws.ivs.player.TextMetadataCue
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.setListener

import java.nio.charset.StandardCharsets

class VideoViewModel(
    private val context: Application,

    ) : BaseViewModel(context) {

    private var player: MediaPlayer? = null
    private var playerListener: Player.Listener? = null
    private val handler = Handler()

    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            progress.value = player?.position?.timeString()
            seekBarProgress.value = player?.position?.toInt()
            seekBarSecondaryProgress.value = player?.bufferedPosition?.toInt()
            handler.postDelayed(this, 500)

        }
    }


    val playerState = MutableLiveData<Player.State>()
    val buttonState = MutableLiveData<PlayingState>()

    val selectedRateValue = MutableLiveData<String>()

    val duration = MutableLiveData<String>()
    val progress = MutableLiveData<String>()

    val durationVisible = MutableLiveData<Boolean>()
    val progressVisible = MutableLiveData<Boolean>()
    val seekBarVisible = MutableLiveData<Boolean>()
    val showControls = MutableLiveData<Boolean>()
    val buffering = MutableLiveData<Boolean>()
    val isPlaying = MutableLiveData<Boolean>()
    val isMute = MutableLiveData<Boolean>()
    val seekBarMax = MutableLiveData<Int>()
    val seekBarProgress = MutableLiveData<Int>()
    val seekBarSecondaryProgress = MutableLiveData<Int>()

    val playerParamsChanged = MutableLiveData<Pair<Int, Int>>()
    val errorHappened = MutableLiveData<Pair<String, String>>()
    val TAG = "VideoActivty"


    init {
        buffering.postValue(true)
        initPlayer()
        setPlayerListener()
        initDefault()

    }

    val PlaybackRate = listOf("2.0", "1.5", "1.0", "0.5")
    val PLAYBACK_RATE_DEFAULT = "1.0"
    private fun initDefault() {
        buttonState.value = PlayingState.PLAYING


        selectedRateValue.value = PLAYBACK_RATE_DEFAULT

    }

    private fun initPlayer() {
        // Media player initialization
        player = MediaPlayer(context)
    }

    private fun setPlayerListener() {
        // Media player listener creation and initialization
        playerListener = player?.setListener(
            onVideoSizeChanged = { width, height ->
                Log.d(TAG, "Video size changed: $width $height")
                playerParamsChanged.value = Pair(width, height)
            },
            onCue = { cue ->
                if (cue is TextMetadataCue) {
                    Log.d(TAG, "Received Text Metadata: ${cue.text}")
                }
            },
            onDurationChanged = { durationValue ->
                Log.d(TAG, "Duration changed: $durationValue")
                if (player?.duration != null && player!!.duration > 0L) {
                    // Switch to VOD player controls
                    seekBarMax.value = durationValue.toInt()

                    duration.value = durationValue.timeString()
                    durationVisible.value = true
                    progressVisible.value = true
                    updateSeekBarTask.run()
                }
            },
            onStateChanged = { state ->
                Log.d(TAG, "State changed: $state")
                playerState.value = state
            },
            onMetadata = { data, buffer ->
                if (MediaType.TEXT_PLAIN == data) {
                    val textData = StandardCharsets.UTF_8.decode(buffer)
                    Log.d(TAG, "Received Timed Metadata: $textData")
                }
            },
            onError = { exception ->
                Log.d(TAG, "Error happened: $exception")
                errorHappened.value = Pair(exception.code.toString(), exception.errorMessage)
                isPlaying.value = false
            }
        )
    }

    fun toggleControls(show: Boolean) {
        Log.d(TAG, "Toggling controls: $show")
        showControls.value = show
        seekBarVisible.value = show
    }

    fun play() {
        Log.d(TAG, "Starting playback")
        // Starts or resumes playback of the stream.
        player?.play()
    }

    fun pause() {
        Log.d(TAG, "Pausing playback")
        // Pauses playback of the stream.
        player?.pause()
    }

    fun playerRelease() {
        Log.d(TAG, "Releasing player")
        // Removes a playback state listener
        playerListener?.let { player?.removeListener(it) }
        // Releases the player instance
        player?.release()
        player = null
    }

    fun playerStart(surface: Surface, url: String?) {
        Log.d(TAG, "Starting player")

//        var urlConst =
//            "https://cdn.videvo.net/videvo_files/video/free/2019-03/large_watermarked/181004_10_LABORATORIES-SCIENCE_08_preview.mp4"
        if (!url.isNullOrEmpty()) {
            initPlayer()
            updateSurface(surface)
            setPlayerListener()
            playerLoadStream(Uri.parse(url))
            play()
        }

    }

    fun playerLoadStream(uri: Uri) {
        Log.d(TAG, "Loading stream URI: $uri")
        // Loads the specified stream
        player?.load(uri)
    }

    fun updateSurface(surface: Surface?) {
        Log.d(TAG, "Updating player surface: $surface")
        // Sets the Surface to use for rendering video
        player?.setSurface(surface)
    }

    fun playerSeekTo(position: Long) {
        Log.d(TAG, "Updating player position: $position")
        // Seeks to a specified position in the stream, in milliseconds
        player?.seekTo(position)
        progress.value = player?.position?.timeString()
    }

    fun mutePlayer() {
        if (player?.isMuted == false) {
            player?.isMuted = true
            isMute.postValue(true)
        } else {
            isMute.postValue(false)
            player?.isMuted = false
        }


    }


//    fun getPlayBackRates(): List<OptionDataItem> {
//        return Configuration.PlaybackRate.toMutableList().map {
//            OptionDataItem(
//                it,
//                selectedRateValue.value == it || selectedQualityValue.value == Configuration.PLAYBACK_RATE_DEFAULT
//            )
//        }
//    }

    fun setPlaybackRate(option: String) {
        Log.d(TAG, "Setting playback rate: $option")
        player?.playbackRate = option.toFloat()
        selectedRateValue.value = option
    }


}
