package com.fypmoney.view.adapter


import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.*
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewhelper.FeedsViewHelper
import com.fypmoney.viewmodel.StoreScreenViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


/**
 * This adapter class is used to handle feeds
 */
class FeedsStoreAdapter(
    var context:Activity,
    var viewModel: StoreScreenViewModel,
    var onFeedItemClickListener: FeedsAdapter.OnFeedItemClickListener
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var feedList: ArrayList<FeedDetails>? = ArrayList()
    private val typeWithTitle = 1

    private val typeWithoutTitle = 2
    private val typeVideo = 3
    private val staticImage1x1_5 = 6
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            typeWithoutTitle -> {
                val mRowBinding = ItemStoreFeedsBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return DiduKnowViewHolder(mRowBinding)
            }
            typeWithTitle -> {
                val mRowBinding = FeedsRowLayoutShortBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return ViewHolder(mRowBinding)
            }
            typeVideo -> {
                val mRowBinding = FeedRowLayoutVideoviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return VideoViewHolder(mRowBinding)
            }
            staticImage1x1_5 -> {
                val mRowBinding = ItemFeedsStaticImageAspectRatioBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1_5(mRowBinding)
            }
        }
        return ViewHolder()
    }

    override fun getItemCount(): Int {
        return feedList!!.size
    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(feedList1: List<FeedDetails>?) {

        feedList1?.forEach {
            feedList!!.add(it)
        }
        notifyDataSetChanged()
    }

    inner class StaticImage1x1_5(
        private val mRowItemBinding: ItemFeedsStaticImageAspectRatioBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper


            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCount.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.page.set(viewModel.page.get()!! + 1)
                    viewModel.callFetchFeedsApi(
                        latitude = viewModel.latitude.get(),
                        longitude = viewModel.longitude.get()
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mRowItemBinding.executePendingBindings()

        }
    }
    inner class DiduKnowViewHolder(
        private val mRowItemBinding: ItemStoreFeedsBannerBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            if (mRowItemBinding != null) {
                Utility.setImageUsingGlideWithShimmerPlaceholder(context,feedList?.get(position)?.resourceId,mRowItemBinding.image)
            }
             mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper

            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCount.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.page.set(viewModel.page.get()!! + 1)
                    viewModel.callFetchFeedsApi(
                        latitude = viewModel.latitude.get(),
                        longitude = viewModel.longitude.get()
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mRowItemBinding.executePendingBindings()

        }


    }

    override fun getItemViewType(position: Int): Int {
        return when (feedList?.get(position)?.displayCard) {
            AppConstants.FEED_TYPE_BLOG -> {
                typeWithTitle
            }
            AppConstants.FEED_TYPE_VIDEO -> {
                typeVideo
            }
            AppConstants.FEED_TYPE_STATIC_IMAGE1x1_5 -> {
                staticImage1x1_5
            }

            else -> {
                typeWithoutTitle
            }
        }


    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: FeedsRowLayoutShortBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener, 1
            )
            mRowItemBinding!!.viewHelper = mViewHelper



            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCount.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.page.set(viewModel.page.get()!! + 1)
                    viewModel.callFetchFeedsApi(
                        latitude = viewModel.latitude.get(),
                        longitude = viewModel.longitude.get()
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mRowItemBinding.executePendingBindings()

        }


    }



    inner class VideoViewHolder(
        private val mRowItemBinding: FeedRowLayoutVideoviewBinding? = null,
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )

            mRowItemBinding?.youtubePlayerView?.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    try {
                        youTubePlayer.cueVideo(
                            feedList?.get(position)?.resourceId!!, 0f
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            })

            mRowItemBinding?.executePendingBindings()

        }


    }


    interface OnFeedItemClickListener {
        fun onFeedClick(position: Int, feedDetails: FeedDetails)
    }


}