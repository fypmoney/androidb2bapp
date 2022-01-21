package com.fypmoney.util.videoplayer


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.ivs.player.*
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityVideoXploreBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.launchMain
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.activity_video2.*
import kotlinx.android.synthetic.main.activity_video_xplore.*
import kotlinx.android.synthetic.main.activity_video_xplore.play_button_view
import kotlinx.android.synthetic.main.activity_video_xplore.player_controls
import kotlinx.android.synthetic.main.activity_video_xplore.player_root
import kotlinx.android.synthetic.main.activity_video_xplore.seek_bar
import kotlinx.android.synthetic.main.activity_video_xplore.status_text
import kotlinx.android.synthetic.main.activity_video_xplore.surface_view
import kotlinx.android.synthetic.main.toolbar.*

import java.util.ArrayList
import java.util.*

class VideoActivityWithExplore : BaseActivity<ActivityVideoXploreBinding, VideoExploreViewModel>(),
    SurfaceHolder.Callback {

    private var mViewBinding: ActivityVideoXploreBinding? = null
    private lateinit var viewModel: VideoExploreViewModel
    private val timerHandler = Handler()

    val HIDE_CONTROLS_DELAY = 1800L
    private val timerRunnable = kotlinx.coroutines.Runnable {
        launchMain {
            Log.d(TAG, "Hiding controls")

            viewModel.toggleControls(false)
        }
    }
    val TAG = "Videoplayer"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewDataBinding()
        mViewBinding!!.data = viewModel
        val url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        val ACTIONFLAG = intent?.getStringExtra(AppConstants.ACTIONFLAG)
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            backArrowTint = Color.WHITE
        )
        viewModel.callExplporeContent(ACTIONFLAG)

        viewModel?.rewardHistoryList.observe(
            this,
            { list ->

                setRecyclerView(mViewBinding!!, list)
            })
        viewModel?.openBottomSheet.observe(
            this,
            { list ->

                callOfferDetailsSheeet(list[0])
            })

        viewModel?.feedDetail.observe(
            this,
            { list ->

                when (list.displayCard) {

                    AppConstants.FEED_TYPE_BLOG -> {

                        intentToActivitytoBlog(
                            UserFeedsDetailView::class.java,
                            list,
                            AppConstants.FEED_TYPE_BLOG
                        )
                    }
                    AppConstants.FEED_TYPE_STORIES -> {

                        callDiduKnowBottomSheet(list.resourceArr)
                    }

                }


            })

        surface_view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
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

    private fun callOfferDetailsSheeet(redeemDetails: offerDetailResponse) {

        var bottomSheetMessage = OfferDetailsBottomSheet(redeemDetails)
        bottomSheetMessage.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage.show(supportFragmentManager, "TASKMESSAGE")
    }

    private fun callDiduKnowBottomSheet(list: List<String>) {
        val bottomSheet =
            StoriesBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "DidUKnowSheet")
    }

    private fun intentToActivitytoBlog(
        aClass: Class<*>,
        feedDetails: FeedDetails,
        type: String? = null
    ) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }

    private fun setRecyclerView(
        root: ActivityVideoXploreBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size > 0) {
            root.shimmerLayout.visibility = View.GONE
        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        root.rvExplore.layoutManager = layoutManager

        var arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        var exploreClickListener2 = object : ExploreItemClickListener {

            override fun onItemClicked(
                position: Int,
                it: SectionContentItem,
                exploreContentResponse: ExploreContentResponse?
            ) {
                when (it.redirectionType) {
                    AppConstants.EXPLORE_IN_APP -> {
                        it.redirectionResource?.let { uri ->

                            val redirectionResources = uri?.split(",")?.get(0)
                            if (redirectionResources == AppConstants.FyperScreen) {
                                navController?.navigate(R.id.navigation_fyper)
                            } else if (redirectionResources == AppConstants.JACKPOTTAB) {
                                navController?.navigate(R.id.navigation_jackpot)
                            } else if (redirectionResources == AppConstants.CardScreen) {
                                navController?.navigate(R.id.navigation_card)
                            } else if (redirectionResources == AppConstants.RewardHistory) {
                                navController?.navigate(R.id.navigation_rewards_history)
                            }else if (redirectionResources == AppConstants.ARCADE) {
                                navController?.navigate(R.id.navigation_arcade)
                            } else {

                                redirectionResources?.let { it1 ->
                                    Utility.deeplinkRedirection(
                                        it1,
                                        this@VideoActivityWithExplore
                                    )
                                }
                            }


                        }

                    }
                    AppConstants.EXT_WEBVIEW -> {
                        val intent =
                            Intent(this@VideoActivityWithExplore, VideoActivity::class.java)
                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)

                        startActivity(intent)

                    }
                    AppConstants.EXPLORE_IN_APP_WEBVIEW -> {

                        val intent =
                            Intent(this@VideoActivityWithExplore, ExploreInAppWebview::class.java)
//        intent.putExtra(AppConstants.EXPLORE_RESPONSE, feedDetails)
                        intent.putExtra(
                            AppConstants.FROM_WHICH_SCREEN,
                            AppConstants.EXPLORE_IN_APP_WEBVIEW
                        )
                        intent.putExtra(AppConstants.IN_APP_URL, it.redirectionResource)

                        startActivity(intent)
                    }
                    AppConstants.IN_APP_WITH_CARD -> {

                        val intent =
                            Intent(this@VideoActivityWithExplore, StoreWebpageOpener2::class.java)
//        intent.putExtra(AppConstants.EXPLORE_RESPONSE, feedDetails)

                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)
                        startActivity(intent)
                    }
                    AppConstants.OFFER_REDIRECTION -> {
                        viewModel.callFetchOfferApi(it.redirectionResource)

                    }


                    AppConstants.FEED_TYPE_BLOG -> {
                        viewModel.callFetchFeedsApi(it.redirectionResource)

                    }

                    AppConstants.EXT_WEBVIEW -> {
                        if (it.redirectionResource != null) {
                            startActivity(
                                Intent.createChooser(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(it.redirectionResource)
                                    ), getString(R.string.browse_with)
                                )
                            )
                        }


                    }
                    AppConstants.EXPLORE_TYPE_STORIES -> {
                        if (!it.redirectionResource.isNullOrEmpty()) {
//

                            viewModel.callFetchFeedsApi(it.redirectionResource)

                        }

                    }
                }

            }
        }
        val scale: Float = this.resources.displayMetrics.density
        val typeAdapter = ExploreBaseAdapter(
            arrayList,
            this,
            exploreClickListener2,
            scale
        )
        root.rvExplore.adapter = typeAdapter
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

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video_xplore
    }

    override fun getViewModel(): VideoExploreViewModel {
        viewModel = ViewModelProvider(this).get(VideoExploreViewModel::class.java)
        return viewModel
    }

}
