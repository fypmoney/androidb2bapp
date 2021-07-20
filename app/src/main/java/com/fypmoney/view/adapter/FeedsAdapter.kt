package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.FeedRowLayoutVideoviewBinding
import com.fypmoney.databinding.FeedsRowLayoutBinding
import com.fypmoney.databinding.FeedsRowLayoutHorizontalBinding
import com.fypmoney.databinding.FeedsRowLayoutVerticalBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewhelper.FeedsViewHelper
import com.fypmoney.viewmodel.FeedsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import java.lang.Exception


/**
 * This adapter class is used to handle feeds
 */
class FeedsAdapter(
    var viewModel: FeedsViewModel,
    var onFeedItemClickListener: OnFeedItemClickListener
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var feedList: ArrayList<FeedDetails>? = ArrayList()
    private val typeWithTitle = 1
    private val typeWithoutTitle = 2
    private val typeVideo = 3
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            typeWithTitle, typeWithoutTitle -> {
                val mRowBinding = FeedsRowLayoutBinding.inflate(
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
        }
        return ViewHolder()
    }

    override fun getItemCount(): Int {
        return feedList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (feedList?.get(position)?.displayCard) {
            AppConstants.FEED_TYPE_BLOG -> {
                typeWithTitle
            }
            AppConstants.FEED_TYPE_VIDEO -> {
                typeVideo
            }
            else -> {
                typeWithoutTitle
            }
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (feedList?.get(position)?.displayCard) {
            AppConstants.FEED_TYPE_BLOG -> {

            }
        }
        return holder.onBind(position)
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

    inner class ViewHolder(
        private val mRowItemBinding: FeedsRowLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel


            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCount.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.pageValue.set(viewModel.pageValue.get()!! + 1)
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
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding?.youtubePlayerView?.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    if (URLUtil.isValidUrl(feedList?.get(position)?.resourceId!!)) {
                        try {
                            youTubePlayer.cueVideo(
                                feedList?.get(position)?.resourceId?.split("=")?.get(1)!!, 0f
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                }
            })
            try {
                if (position == feedList?.size!! - 1 && viewModel.totalCount.get()!! > feedList?.size!!) {
                    viewModel.isApiLoading.set(true)
                    viewModel.pageValue.set(viewModel.pageValue.get()!! + 1)
                    viewModel.callFetchFeedsApi(
                        latitude = viewModel.latitude.get(),
                        longitude = viewModel.longitude.get()
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mRowItemBinding?.executePendingBindings()

        }


    }

    interface OnFeedItemClickListener {
        fun onFeedClick(feedDetails: FeedDetails)
    }

}