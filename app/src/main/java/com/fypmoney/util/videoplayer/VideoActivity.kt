package com.fypmoney.util.videoplayer

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.ivs.player.*
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity

import com.fypmoney.databinding.ActivityVideoBinding
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import java.nio.ByteBuffer
import java.util.ArrayList

class VideoActivity : BaseActivity<ActivityVideoBinding, VideoVM>() {

    private var mViewBinding: ActivityVideoBinding? = null
    private lateinit var mViewModel1: VideoVM

    val TAG = "Videoplayer"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewDataBinding()
        val url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        val pageTitle = intent?.getStringExtra(ARG_WEB_PAGE_TITLE)


        mViewBinding?.playerView?.player?.load(Uri.parse("https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.DmumNckWFTqz.m3u8"))


    }


    override fun onStart() {
        super.onStart()
        mViewBinding?.playerView?.player?.play()
    }

    override fun onStop() {
        super.onStop()
        mViewBinding?.playerView?.player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding?.playerView?.player?.release()
    }


    private fun handlePlayerEvents() {
        mViewBinding?.playerView?.player?.apply {
            // Listen to changes on the player
            addListener(object : Player.Listener() {
                override fun onAnalyticsEvent(p0: String, p1: String) {}
                override fun onDurationChanged(p0: Long) {
                    // If the video is a VOD, you can seek to a duration in the video
                    Log.i("IVSPlayer", "New duration: $duration")
                    seekTo(p0)
                }

                override fun onError(p0: PlayerException) {}
                override fun onMetadata(type: String, data: ByteBuffer) {}
                override fun onQualityChanged(p0: Quality) {
                    Log.i("IVSPlayer", "Quality changed to " + p0);
                    updateQuality()
                }

                override fun onRebuffering() {}
                override fun onSeekCompleted(p0: Long) {}
                override fun onVideoSizeChanged(p0: Int, p1: Int) {}
                override fun onCue(cue: Cue) {
                    when (cue) {
                        is TextMetadataCue -> Log.i(
                            "IVSPlayer",
                            "Received Text Metadata: ${cue.text}"
                        )
                    }
                }

                override fun onStateChanged(state: Player.State) {
                    Log.i("PlayerLog", "Current state: ${state}")
                    when (state) {
                        Player.State.BUFFERING,
                        Player.State.READY -> {
                            updateQuality()
                        }
                        Player.State.IDLE,
                        Player.State.ENDED -> {
                            // no-op
                        }
                        Player.State.PLAYING -> {
                            // Qualities will be dependent on the video loaded, and can
                            // be retrieved from the player
                            Log.i("IVSPlayer", "Available Qualities: ${qualities}")
                        }
                    }
                }
            })
        }
    }

    private fun updateQuality() {
        val qualitySpinner = mViewBinding?.qualitySpinner
        var auto = "auto"
        val currentQuality: String = mViewBinding?.playerView?.player?.getQuality()?.getName()!!
        if (mViewBinding?.playerView?.player?.isAutoQualityMode() == true && !TextUtils.isEmpty(
                currentQuality
            )
        ) {
            auto += " ($currentQuality)"
        }
        var selected = 0
        val names: ArrayList<String?> = ArrayList()
        for (quality in mViewBinding?.playerView?.player?.getQualities()!!) {
            names.add(quality.name)
        }
        names.add(0, auto)
        if (mViewBinding?.playerView?.player?.isAutoQualityMode() != true) {
            for (i in 0 until names.size) {
                if (names.get(i).equals(currentQuality)) {
                    selected = i
                }
            }
        }
        val qualityAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
            this,
            android.R.layout.simple_spinner_item, names
        )
        qualityAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        qualitySpinner?.setOnItemSelectedListener(null)
        qualitySpinner?.setAdapter(qualityAdapter)
        qualitySpinner?.setSelection(selected, false)
        qualitySpinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val name = qualityAdapter.getItem(position)
                if (name != null && name.startsWith("auto")) {
                    mViewBinding?.playerView?.player?.setAutoQualityMode(true)
                } else {
                    for (quality in mViewBinding?.playerView?.player?.getQualities()!!) {
                        if (quality.name.equals(name, ignoreCase = true)) {
                            Log.i("IVSPlayer", "Quality Selected: " + quality);
                            mViewBinding?.playerView?.player?.setQuality(quality)
                            break
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video
    }

    override fun getViewModel(): VideoVM {
        mViewModel1 = ViewModelProvider(this).get(VideoVM::class.java)
        return mViewModel1
    }

}
