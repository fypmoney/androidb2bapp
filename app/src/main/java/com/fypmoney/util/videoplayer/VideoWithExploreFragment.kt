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
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.ivs.player.Player
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentVideoWithExploreBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.launchMain
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.storeoffers.OfferDetailsBottomSheet
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*

class VideoWithExploreFragment : BaseFragment<FragmentVideoWithExploreBinding, VideoWithExploreFragmentVM>(),
    SurfaceHolder.Callback {

    private lateinit var mViewBinding: FragmentVideoWithExploreBinding
    private  val videoWithExploreFragmentVM by viewModels<VideoWithExploreFragmentVM> { defaultViewModelProviderFactory }
    private val timerHandler = Handler()

    private val HIDE_CONTROLS_DELAY = 1800L
    private val navArgs:VideoWithExploreFragmentArgs by navArgs()

    private val timerRunnable by lazy {
        kotlinx.coroutines.Runnable {
        launchMain {
            Log.d(TAG, "Hiding controls")

            videoWithExploreFragmentVM.toggleControls(false)
        }
    }
    }
    val TAG = VideoWithExploreFragment::class.java.simpleName


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.data = videoWithExploreFragmentVM
        videoWithExploreFragmentVM.videoUrl = navArgs.videoUrl
        videoWithExploreFragmentVM.actionFlag = navArgs.actionFlag
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            backArrowTint = Color.WHITE
        )
        videoWithExploreFragmentVM.callExplporeContent(videoWithExploreFragmentVM.actionFlag)

        videoWithExploreFragmentVM.rewardHistoryList.observe(
            viewLifecycleOwner
        ) { list ->

            setRecyclerView(mViewBinding, list)
        }
        videoWithExploreFragmentVM.openBottomSheet.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.size > 0) {
                callOfferDetailsSheeet(list[0])
            }
        }

        videoWithExploreFragmentVM.feedDetail.observe(
            viewLifecycleOwner
        ) { list ->

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


        }

        mViewBinding.surfaceView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                val width = videoWithExploreFragmentVM.playerParamsChanged.value?.first
                val height = videoWithExploreFragmentVM.playerParamsChanged.value?.second
                if (width != null && height != null) {
                    mViewBinding.surfaceView.post {
                        Log.d(
                            TAG,
                            "On rotation player layout params changed $width $height"
                        )
                        val prams = ViewGroup.LayoutParams(width, height)
                        mViewBinding.surfaceView.layoutParams = prams
                    }
                }
            }
        }

        mViewBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser) {
                    videoWithExploreFragmentVM.playerSeekTo(progress.toLong())
                }
            }
        })
        mViewBinding.mute.setOnClickListener(View.OnClickListener {
            videoWithExploreFragmentVM.mutePlayer()

        })
        videoWithExploreFragmentVM.playerState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                Player.State.BUFFERING -> {
                    // Indicates that the Player is buffering content
                    videoWithExploreFragmentVM.buffering.value = true
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PLAYING
                    mViewBinding.statusText.setTextColor(Color.WHITE)
                    mViewBinding.statusText.text = getString(R.string.buffering)
                }
                Player.State.IDLE -> {
                    // Indicates that the Player is idle
                    videoWithExploreFragmentVM.buffering.value = false
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PAUSED
                    mViewBinding.statusText.setTextColor(Color.WHITE)
                    mViewBinding.statusText.text = getString(R.string.paused)
                }
                Player.State.READY -> {
                    // Indicates that the Player is ready to play the loaded source
                    videoWithExploreFragmentVM.buffering.value = false
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PAUSED
                    mViewBinding.statusText.setTextColor(Color.WHITE)
                    mViewBinding.statusText.text = getString(R.string.paused)
                }
                Player.State.ENDED -> {
                    // Indicates that the Player reached the end of the stream
                    videoWithExploreFragmentVM.buffering.value = false
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PAUSED
                    mViewBinding.statusText.setTextColor(Color.WHITE)
                    mViewBinding.statusText.text = getString(R.string.ended)
                }
                Player.State.PLAYING -> {
                    // Indicates that the Player is playing
                    videoWithExploreFragmentVM.buffering.value = false
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PLAYING
                    mViewBinding.statusText.setTextColor(Color.WHITE)
                    mViewBinding.statusText.text = getString(R.string.playing)
                }
                else -> { /* Ignored */
                }
            }
        })

        videoWithExploreFragmentVM.buttonState.observe(viewLifecycleOwner) { state ->
            videoWithExploreFragmentVM.isPlaying.value = state == PlayingState.PLAYING
        }

        videoWithExploreFragmentVM.playerParamsChanged.observe(viewLifecycleOwner) {
            Log.d(TAG, "Player layout params changed ${it.first} ${it.second}")
            //ViewUtil.setLayoutParams(mViewBinding.surfaceView, it.first, it.second)
            /*val prams = ViewGroup.LayoutParams(it.first, it.second)
            mViewBinding.surfaceView.layoutParams = prams*/
        }

        videoWithExploreFragmentVM.errorHappened.observe(viewLifecycleOwner) {
            Log.d(TAG, "Error dialog is shown")

        }

        initSurface()
        initButtons()
        videoWithExploreFragmentVM.playerStart(mViewBinding.surfaceView.holder.surface, videoWithExploreFragmentVM.videoUrl)
    }

    private fun initSurface() {
        mViewBinding.surfaceView.holder.addCallback(this)
        mViewBinding.playerRoot.setOnClickListener {
            Log.d(TAG, "Player screen clicked")
            when (mViewBinding.playerControls.visibility) {
                View.VISIBLE -> {
                    videoWithExploreFragmentVM.toggleControls(false)
                }
                View.GONE -> {
                    videoWithExploreFragmentVM.toggleControls(true)
                    restartTimer()
                }
            }
        }


    }

    private fun initButtons() {
        mViewBinding.playerControls.setOnClickListener {
            restartTimer()
            when (videoWithExploreFragmentVM.buttonState.value) {
                PlayingState.PLAYING -> {
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PAUSED
                    videoWithExploreFragmentVM.pause()
                }
                PlayingState.PAUSED -> {
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PLAYING
                    videoWithExploreFragmentVM.play()
                }
            }
        }
        mViewBinding.playButtonView.setOnClickListener {
            restartTimer()
            when (videoWithExploreFragmentVM.buttonState.value) {
                PlayingState.PLAYING -> {
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PAUSED
                    videoWithExploreFragmentVM.pause()
                }
                PlayingState.PAUSED -> {
                    videoWithExploreFragmentVM.buttonState.value = PlayingState.PLAYING
                    videoWithExploreFragmentVM.play()
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
        videoWithExploreFragmentVM.updateSurface(null)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "Surface created")
        videoWithExploreFragmentVM.updateSurface(holder.surface)
    }

    private fun callOfferDetailsSheeet(redeemDetails: offerDetailResponse) {

        val bottomSheetMessage = OfferDetailsBottomSheet(redeemDetails)
        bottomSheetMessage.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage.show(requireActivity().supportFragmentManager, "TASKMESSAGE")
    }

    private fun callDiduKnowBottomSheet(list: List<String>) {
        val bottomSheet =
            StoriesBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(requireActivity().supportFragmentManager, "DidUKnowSheet")
    }

    private fun intentToActivitytoBlog(
        aClass: Class<*>,
        feedDetails: FeedDetails,
        type: String? = null
    ) {
        val intent = Intent(requireActivity(), aClass)
        intent.putExtra(AppConstants.FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }

    private fun setRecyclerView(
        root: FragmentVideoWithExploreBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size > 0) {
            root.shimmerLayout.visibility = View.GONE
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.rvExplore.layoutManager = layoutManager

        val arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        val exploreClickListener2 = object : ExploreItemClickListener {

            override fun onItemClicked(
                position: Int,
                it: SectionContentItem,
                exploreContentResponse: ExploreContentResponse?
            ) {
                when (it.redirectionType) {
                    AppConstants.EXPLORE_IN_APP -> {
                        it.redirectionResource?.let { uri ->
                            when (val redirectionResources = uri.split(",")[0]) {
                                AppConstants.FyperScreen -> {
                                    findNavController().navigate(R.id.navigation_fyper)
                                }
                                AppConstants.JACKPOTTAB -> {
                                    findNavController().navigate(R.id.navigation_jackpot)
                                }
                                AppConstants.CardScreen -> {
                                    findNavController().navigate(R.id.navigation_card)
                                }
                                AppConstants.RewardHistory -> {
                                    findNavController().navigate(R.id.navigation_rewards_history)
                                }
                                AppConstants.ARCADE -> {
                                    findNavController().navigate(R.id.navigation_arcade)
                                }
                                AppConstants.F_Store -> {
                                    findNavController().navigate(R.id.navigation_explore)
                                }
                                AppConstants.REWARDS -> {
                                    findNavController().navigate(R.id.navigation_rewards)
                                }
                                AppConstants.INSIGHTS -> {
                                    findNavController().navigate(R.id.navigation_insights)
                                }
                                else -> {

                                    redirectionResources.let { it1 ->
                                        Utility.deeplinkRedirection(
                                            it1,
                                            requireContext()
                                        )
                                    }
                                }
                            }


                        }

                    }
                    AppConstants.EXT_WEBVIEW -> {
                        val intent =
                            Intent(requireActivity(), VideoActivity::class.java)
                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)

                        startActivity(intent)

                    }
                    AppConstants.EXPLORE_IN_APP_WEBVIEW -> {

                        val intent =
                            Intent(requireActivity(), ExploreInAppWebview::class.java)
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
                            Intent(requireActivity(), StoreWebpageOpener2::class.java)
//        intent.putExtra(AppConstants.EXPLORE_RESPONSE, feedDetails)

                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)
                        startActivity(intent)
                    }
                    AppConstants.OFFER_REDIRECTION -> {
                        videoWithExploreFragmentVM.callFetchOfferApi(it.redirectionResource)

                    }


                    AppConstants.FEED_TYPE_BLOG -> {
                        videoWithExploreFragmentVM.callFetchFeedsApi(it.redirectionResource)

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

                            videoWithExploreFragmentVM.callFetchFeedsApi(it.redirectionResource)

                        }

                    }
                }

            }
        }
        val scale: Float = this.resources.displayMetrics.density
        val typeAdapter = ExploreBaseAdapter(
            arrayList,
            requireContext(),
            exploreClickListener2,
            scale
        )
        root.rvExplore.adapter = typeAdapter
    }


    override fun onResume() {
        super.onResume()
        videoWithExploreFragmentVM.play()
    }

    override fun onPause() {
        super.onPause()
        videoWithExploreFragmentVM.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoWithExploreFragmentVM.playerRelease()

        mViewBinding.surfaceView.holder.removeCallback(this)
    }

    override fun onTryAgainClicked() {
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_video_with_explore
    }

    override fun getViewModel(): VideoWithExploreFragmentVM {
        return videoWithExploreFragmentVM
    }

}
