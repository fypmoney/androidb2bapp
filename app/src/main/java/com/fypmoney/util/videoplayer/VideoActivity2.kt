package com.fypmoney.util.videoplayer

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.ivs.player.Player
import com.amazonaws.ivs.player.ViewUtil
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityVideo2Binding
import com.fypmoney.util.launchMain
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.activity_video2.*


class VideoActivity2 : BaseActivity<ActivityVideo2Binding, VideoViewModel>(),
    SurfaceHolder.Callback {
    private lateinit var viewModel: VideoViewModel
    val HIDE_CONTROLS_DELAY = 1800L

    val TAG = "VideoPlayer"
    private val timerHandler = Handler()
    private val timerRunnable = kotlinx.coroutines.Runnable {
        launchMain {
            Log.d(TAG, "Hiding controls")

            viewModel.toggleControls(false)
        }
    }




    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            player_controls_root.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        } else {
            player_controls_root.layoutParams.width =
                resources.getDimension(R.dimen.cb_column_width).toInt()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityVideo2Binding>(this, R.layout.activity_video2)
            .apply {
                data = viewModel
                lifecycleOwner = this@VideoActivity2
            }
        val url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        // Surface view listener for rotation handling
        surface_view.addOnLayoutChangeListener(
            object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        val width = viewModel.playerParamsChanged.value?.first
                        val height = viewModel.playerParamsChanged.value?.second
                        if (width != null && height != null) {
                            surface_view.post {
                                Log.d(
                                    TAG,
                                    "On rotation player layout params changed $width $height"
                                )
                                ViewUtil.setLayoutParams(surface_view, width, height)
                            }
                        }
                    }
                }
            }


        )

        mute.setOnClickListener(View.OnClickListener {
            viewModel.mutePlayer()

        })
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                restartTimer()
                if (fromUser) {
                    viewModel.playerSeekTo(progress.toLong())
                }
            }
        })


        viewModel.playerState.observe(this, Observer { state ->
            when (state) {
                Player.State.BUFFERING -> {
                    // Indicates that the Player is buffering content
                    viewModel.buffering.value = true
                    viewModel.buttonState.value = PlayingState.PLAYING
                    status_text.setTextColor(Color.WHITE)
                    status_text.text = getString(R.string.buffering)
                }
                Player.State.IDLE -> {
                    // Indicates that the Player is idle
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PAUSED
                    status_text.setTextColor(Color.WHITE)
                    status_text.text = getString(R.string.paused)
                }
                Player.State.READY -> {
                    // Indicates that the Player is ready to play the loaded source
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PAUSED
                    status_text.setTextColor(Color.WHITE)
                    status_text.text = getString(R.string.paused)
                }
                Player.State.ENDED -> {
                    // Indicates that the Player reached the end of the stream
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PAUSED
                    status_text.setTextColor(Color.WHITE)
                    status_text.text = getString(R.string.ended)
                }
                Player.State.PLAYING -> {
                    // Indicates that the Player is playing
                    viewModel.buffering.value = false
                    viewModel.buttonState.value = PlayingState.PLAYING
                    status_text.setTextColor(Color.WHITE)
                    status_text.text = getString(R.string.playing)
                }
                else -> { /* Ignored */
                }
            }
        })

        viewModel.buttonState.observe(this, Observer { state ->
            viewModel.isPlaying.value = state == PlayingState.PLAYING
        })

        viewModel.playerParamsChanged.observe(this, Observer {
            Log.d(TAG, "Player layout params changed ${it.first} ${it.second}")
            ViewUtil.setLayoutParams(surface_view, it.first, it.second)
        })

        viewModel.errorHappened.observe(this, Observer {
            Log.d(TAG, "Error dialog is shown")

        })

        initSurface()
        initButtons()
        viewModel.playerStart(surface_view.holder.surface, url)
    }


    override fun onResume() {
        super.onResume()
        viewModel.play()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.playerRelease()

        surface_view.holder.removeCallback(this)
    }

    private fun initSurface() {
        surface_view.holder.addCallback(this)
        player_root.setOnClickListener {
            Log.d(TAG, "Player screen clicked")
            when (player_controls.visibility) {
                View.VISIBLE -> {
                    viewModel.toggleControls(false)
                }
                View.GONE -> {
                    viewModel.toggleControls(true)
                    restartTimer()
                }
            }
        }

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                restartTimer()
                if (fromUser) {
                    viewModel.playerSeekTo(progress.toLong())
                }
            }
        })
    }

    private fun initButtons() {
        player_controls.setOnClickListener {
            restartTimer()
            when (viewModel.buttonState.value) {
                PlayingState.PLAYING -> {
                    viewModel.buttonState.value = PlayingState.PAUSED
                    viewModel.pause()
                }
                PlayingState.PAUSED -> {
                    viewModel.buttonState.value = PlayingState.PLAYING
                    viewModel.play()
                }
            }
        }
        play_button_view.setOnClickListener {
            restartTimer()
            when (viewModel.buttonState.value) {
                PlayingState.PLAYING -> {
                    viewModel.buttonState.value = PlayingState.PAUSED
                    viewModel.pause()
                }
                PlayingState.PAUSED -> {
                    viewModel.buttonState.value = PlayingState.PLAYING
                    viewModel.play()
                }
            }
        }





        restartTimer()
    }

    private fun restartTimer() {
        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.postDelayed(timerRunnable, HIDE_CONTROLS_DELAY)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        /* Ignored */
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "Surface destroyed")
        viewModel.updateSurface(null)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created")
        viewModel.updateSurface(holder.surface)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video2
    }

    override fun getViewModel(): VideoViewModel {
        viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        return viewModel!!
    }
}
