package com.fypmoney.view.adapter


import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.*
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewhelper.FeedsViewHelper
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsJackpotVM
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


/**
 * This adapter class is used to handle feeds
 */
class FeedsRewardsJackpotAdapter(
    var context: Activity,
    var viewModel: RewardsJackpotVM,
    var onFeedItemClickListener: FeedsAdapter.OnFeedItemClickListener,
    var feedList: ArrayList<FeedDetails>? = ArrayList()
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private val typeWithTitle = 1

    private val typeWithoutTitle = 2
    private val typeVideo = 3
    private val staticImage1x1 = 4
    private val inAppwebview1x1 = 5
    private val staticImage1x1_5 = 6
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            typeWithoutTitle -> {
                val mRowBinding = FeedsDidUKnowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return DiduKnowViewHolder(mRowBinding)
            }
            typeWithTitle -> {
                val mRowBinding = FeedsRowLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return ViewHolder(mRowBinding)
            }
            staticImage1x1 -> {
                val mRowBinding = FeedsDidUKnowrewards1x1Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1(mRowBinding)
            }
            staticImage1x1_5 -> {
                val mRowBinding = ItemFeedsStaticImageAspectRatioBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1_5(mRowBinding)
            }
            inAppwebview1x1 -> {
                val mRowBinding = FeedsDidUKnowrewards1x1Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1(mRowBinding)
            }
            typeVideo -> {
                val mRowBinding = FeedRowLayoutVideoviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return VideoViewHolder(mRowBinding)
            }
        }
        return ViewHolder()
    }

    inner class StaticImage1x1(
        private val mRowItemBinding: FeedsDidUKnowrewards1x1Binding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper



            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCountJackpot.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.pagejackpot.set(viewModel.pagejackpot.get()!! + 1)
                    viewModel.callFetchFeedsJackpotApi(
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

    override fun getItemCount(): Int {
        return feedList!!.size
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
                if (position == feedList?.size!! - 1 && viewModel.totalCountJackpot.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.pagejackpot.set(viewModel.pagejackpot.get()!! + 1)
                    viewModel.callFetchFeedsJackpotApi(
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

    /**
     * This will set the data in the list in adapter
     */
    fun setList(feedList1: List<FeedDetails>?) {

        if (feedList1 != null) {
            feedList1?.forEach {
                feedList!!.add(it)
            }
        } else {
            feedList?.clear()
        }

        notifyDataSetChanged()
    }

    inner class DiduKnowViewHolder(
        private val mRowItemBinding: FeedsDidUKnowBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            if (mRowItemBinding != null) {
                Utility.setImageUsingGlideWithShimmerPlaceholder(
                    context,
                    feedList?.get(position)?.resourceId,
                    mRowItemBinding.image
                )
            }
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper

            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCountJackpot.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.pagejackpot.set(viewModel.pagejackpot.get()!! + 1)
                    viewModel.callFetchFeedsJackpotApi(
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
            AppConstants.FEED_TYPE_STATIC_IMAGE1x1 -> {
                staticImage1x1
            }

            AppConstants.FEED_TYPE_STATIC_IMAGE1x1_5 -> {
                staticImage1x1_5
            }
            AppConstants.IN_APP_WEBVIEW1x1 -> {
                inAppwebview1x1
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
        private val mRowItemBinding: FeedsRowLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener, 1
            )
            mRowItemBinding!!.viewHelper = mViewHelper



            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCountJackpot.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.pagejackpot.set(viewModel.pagejackpot.get()!! + 1)
                    viewModel.callFetchFeedsJackpotApi(
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