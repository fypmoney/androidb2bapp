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
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.setListener
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import java.nio.charset.StandardCharsets

class VideoExploreViewModel(
    private val context: Application,

    ) : BaseViewModel(context) {

    private var player: MediaPlayer? = null
    private var playerListener: Player.Listener? = null
    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()
    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()
    var feedDetail: MutableLiveData<FeedDetails> = MutableLiveData()
    private val handler = Handler()
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            progress.value = player?.position?.timeString()
            seekBarProgress.value = player?.position?.toInt()
            seekBarSecondaryProgress.value = player?.bufferedPosition?.toInt()
            handler.postDelayed(this, 500)
        }
    }
    private val url = MutableLiveData<String>()
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
        url.value =
            "https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.xhP3ExfcX8ON.m3u8"


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

        initPlayer()
        updateSurface(surface)
        setPlayerListener()
        playerLoadStream(Uri.parse(url))
        play()
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


    fun callFetchOfferApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_OFFER_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_OFFER_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_Explore -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<ExploreContentResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)
            }

            ApiConstant.API_FETCH_FEED_DETAILS -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<FeedDetails>(
                    json.get("data").toString(),
                    FeedDetails::class.java
                )

                feedDetail.postValue(array)


            }
            ApiConstant.API_FETCH_OFFER_DETAILS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<offerDetailResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                openBottomSheet.postValue(arrayList)


            }


        }
    }

    fun callFetchFeedsApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_FEED_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_FEED_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    fun callExplporeContent(page: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + page,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }


}
