package com.fypmoney.view.activity

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.ivs.player.ViewUtil
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity

import com.fypmoney.databinding.ActivityVideoBinding

import com.fypmoney.model.VideoViewModel

class VideoActivity : BaseActivity<ActivityVideoBinding, VideoViewModel>(), SurfaceHolder.Callback {

    private var mViewBinding: ActivityVideoBinding? = null
    private lateinit var mViewModel1: VideoViewModel

    val TAG = "Videoplayer"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewDataBinding()






        initUi()
        mViewBinding?.surfaceView?.holder?.surface?.let { mViewModel1.playerStart(it) }
    }


    override fun onResume() {
        super.onResume()
        mViewModel1.play()
    }

    override fun onPause() {
        super.onPause()
        mViewModel1.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel1.release()

        mViewBinding?.surfaceView?.holder?.removeCallback(this)
    }


    private fun initUi() {
        mViewBinding?.surfaceView?.holder?.addCallback(this)


        // Surface view listener for rotation handling
        mViewBinding?.surfaceView?.addOnLayoutChangeListener(
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
                        val width = mViewModel1.playerParamsChanged.value?.first
                        val height = mViewModel1.playerParamsChanged.value?.second
                        if (width != null && height != null) {
                            mViewBinding?.surfaceView?.post {
                                Log.d(
                                    TAG,
                                    "On rotation player layout params changed $width $height"
                                )
                                ViewUtil.setLayoutParams(mViewBinding?.surfaceView, width, height)
                            }
                        }
                    }
                }
            }
        )
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        /* Ignored */
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "Surface destroyed")
        mViewModel1.updateSurface(null)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created")
        mViewModel1.updateSurface(holder.surface)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video
    }

    override fun getViewModel(): VideoViewModel {
        mViewModel1 = ViewModelProvider(this).get(VideoViewModel::class.java)
        return mViewModel1
    }

}
