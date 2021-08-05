package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.FeedRowLayoutVideoviewBinding
import com.fypmoney.databinding.FeedsDidUKnowBinding
import com.fypmoney.databinding.FeedsRowLayoutBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewhelper.FeedsViewHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


/**
 * This adapter class is used to handle feeds
 */
class FeedsSectionAdapter(
    var onFeedItemClickListener: FeedsAdapter.OnFeedItemClickListener
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

    /**
     * This will set the data in the list in adapter
     */
    fun setList(feedList1: List<FeedDetails>?) {
        feedList?.clear()
        feedList1?.forEach {
            feedList!!.add(it)
        }
        notifyDataSetChanged()
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
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: FeedsRowLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener,1
            )
            mRowItemBinding!!.viewHelper = mViewHelper
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
        fun onFeedClick(position:Int,feedDetails: FeedDetails)
    }



}