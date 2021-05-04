package com.dreamfolks.baseapp.view.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.dreamfolks.baseapp.base.BaseViewHolder
import com.dreamfolks.baseapp.databinding.FeedRowLayoutVideoviewBinding
import com.dreamfolks.baseapp.databinding.FeedsRowLayoutHorizontalBinding
import com.dreamfolks.baseapp.databinding.FeedsRowLayoutVerticalBinding
import com.dreamfolks.baseapp.model.FeedDetails
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.viewhelper.FeedsViewHelper
import com.dreamfolks.baseapp.viewmodel.FeedsViewModel
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
    private val typeVertical = 1
    private val typeHorizontal = 2
    private val typeVideo = 3
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            typeVertical -> {
                val mRowBinding = FeedsRowLayoutVerticalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return VerticalViewHolder(mRowBinding)
            }
            typeHorizontal -> {
                val mRowBinding = FeedsRowLayoutHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return HorizontalViewHolder(mRowBinding)


            }
            typeVideo -> {
                val mRowBinding = FeedRowLayoutVideoviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return VideoViewHolder(mRowBinding)
            }
        }
        return VerticalViewHolder()
    }

    override fun getItemCount(): Int {
        return feedList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (feedList?.get(position)?.displayCard) {
            AppConstants.FEED_TYPE_HORIZONTAL -> {
                typeHorizontal
            }
            AppConstants.FEED_TYPE_VERTICAL -> {
                typeVertical
            }
            AppConstants.FEED_TYPE_VIDEO -> {
                typeVideo
            }
            else -> {
                typeVertical
            }
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class VerticalViewHolder(
        private val mRowItemBinding: FeedsRowLayoutVerticalBinding? = null
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
                    viewModel.callFetchFeedsApi(latitude = viewModel.latitude.get(),longitude = viewModel.longitude.get())

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
        feedList1?.forEach {
            feedList!!.add(it)
        }
        notifyDataSetChanged()
    }

    inner class HorizontalViewHolder(
        private val mRowItemBinding: FeedsRowLayoutHorizontalBinding? = null
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
                    viewModel.callFetchFeedsApi(latitude = viewModel.latitude.get(),longitude = viewModel.longitude.get())

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
                    viewModel.callFetchFeedsApi(latitude = viewModel.latitude.get(),longitude = viewModel.longitude.get())

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