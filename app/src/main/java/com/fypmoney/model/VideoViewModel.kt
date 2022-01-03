package com.fypmoney.model

import android.app.Application
import android.net.Uri
import android.util.Log
import android.view.Surface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amazonaws.ivs.player.MediaPlayer
import com.amazonaws.ivs.player.MediaType
import com.amazonaws.ivs.player.Player
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.setListener


import java.util.*

class VideoViewModel(
    val context: Application

) : BaseViewModel(context) {

    private var player: MediaPlayer? = null
    private var playerListener: Player.Listener? = null

    val liveStream = MutableLiveData<Boolean>()
    val showLabel = MutableLiveData<Boolean>()
    val url = MutableLiveData<String>()
    val buffering = MutableLiveData<Boolean>()
    val showQuestions = MutableLiveData<Boolean>()
    val playerParamsChanged = MutableLiveData<Pair<Int, Int>>()
    val errorHappened = MutableLiveData<Pair<String, String>>()
    val LINK =
        "https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.xhP3ExfcX8ON.m3u8"

    val question = MutableLiveData<String>()
    val questionChanged = MutableLiveData<Boolean>()

    init {
        url.value = LINK
        initPlayer()

    }

    val TAG = "Videoplayer"
    private fun initPlayer() {
        // Media player initialization
        player = MediaPlayer(context)

        player?.setListener(
            onVideoSizeChanged = { width, height ->
                Log.d(TAG, "Video size changed: $width $height")
                playerParamsChanged.value = Pair(width, height)
            },
            onDurationChanged = { durationValue ->
                Log.d(TAG, "Duration changed: $durationValue")
                liveStream.value = !(player?.duration != null && player!!.duration > 0L)
            },
            onStateChanged = { state ->
                Log.d(TAG, "State changed: $state")
                when (state) {
                    Player.State.BUFFERING -> {
                        buffering.value = true
                        showLabel.value = false
                    }
                    else -> {
                        buffering.value = false
                        showLabel.value = true
                    }
                }
            },
            onError = { exception ->
                Log.d(TAG, "Error happened: $exception")
                errorHappened.value = Pair(exception.code.toString(), exception.errorMessage)
            }
        )
    }

    fun playerStart(surface: Surface) {
        Log.d(TAG, "Starting player")
        updateSurface(surface)
        playerLoadStream(Uri.parse(url.value))
        play()
    }

    fun playerLoadStream(uri: Uri) {
        Log.d(TAG, "Loading stream URI: $uri")
        // Loads the specified stream
        player?.load(uri)

        player?.play()
    }

    fun updateSurface(surface: Surface?) {
        Log.d(TAG, "Updating player surface: $surface")
        // Sets the Surface to use for rendering video
        player?.setSurface(surface)
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

    fun release() {
        Log.d(TAG, "Releasing player")
        // Removes a playback state listener
        playerListener?.let { player?.removeListener(it) }
        // Releases the player instance
        player?.release()
        player = null
    }


}
