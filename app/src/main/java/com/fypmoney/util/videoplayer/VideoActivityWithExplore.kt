package com.fypmoney.util.videoplayer

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import java.nio.ByteBuffer
import java.util.ArrayList

class VideoActivityWithExplore : BaseActivity<ActivityVideoXploreBinding, VideoVM>() {

    private var mViewBinding: ActivityVideoXploreBinding? = null
    private lateinit var mViewModel1: VideoVM

    val TAG = "Videoplayer"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewDataBinding()
        val url = intent?.getStringExtra(ARG_WEB_URL_TO_OPEN)
        val pageTitle = intent?.getStringExtra(ARG_WEB_PAGE_TITLE)


        mViewBinding?.playerView?.player?.load(Uri.parse("https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.DmumNckWFTqz.m3u8"))




        mViewModel1?.rewardHistoryList.observe(
            this,
            { list ->

                setRecyclerView(mViewBinding!!, list)
            })
        mViewModel1?.openBottomSheet.observe(
            this,
            { list ->

                callOfferDetailsSheeet(list[0])
            })

        mViewModel1?.feedDetail.observe(
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
            override fun onItemClicked(position: Int, it: SectionContentItem) {

                when (it.redirectionType) {
                    AppConstants.EXPLORE_IN_APP -> {
                        it.redirectionResource?.let { uri ->

                            val screen = uri.split(",")[0]
                            if (screen == AppConstants.StoreScreen || screen == AppConstants.CardScreen || screen == AppConstants.FEEDSCREEN || screen == AppConstants.FyperScreen) {
//                                tabchangeListner.tabchange(0, screen)
                            } else {

                                Utility.deeplinkRedirection(screen, this@VideoActivityWithExplore)
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
                        mViewModel1.callFetchOfferApi(it.redirectionResource)

                    }


                    AppConstants.FEED_TYPE_BLOG -> {
                        mViewModel1.callFetchFeedsApi(it.redirectionResource)

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

                            mViewModel1.callFetchFeedsApi(it.redirectionResource)

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
        return R.layout.activity_video_xplore
    }

    override fun getViewModel(): VideoVM {
        mViewModel1 = ViewModelProvider(this).get(VideoVM::class.java)
        return mViewModel1
    }

}
