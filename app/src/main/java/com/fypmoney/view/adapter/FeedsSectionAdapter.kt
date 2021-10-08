package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.*
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewhelper.FeedsViewHelper
import com.fypmoney.viewmodel.FeedsViewModel
import com.fypmoney.viewmodel.HomeScreenViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


/**
 * This adapter class is used to handle feeds
 */
class FeedsSectionAdapter(
    var viewModel: HomeScreenViewModel,
    var onFeedItemClickListener: FeedsAdapter.OnFeedItemClickListener
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var feedList: ArrayList<FeedDetails>? = ArrayList()
    private val typeWithTitle = 1
    private val typeWithoutTitle = 2
    private val staticImage1x1 = 4
    private val inAppwebview1x1 = 5
    private val typeVideo = 3
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            typeWithoutTitle -> {
                val mRowBinding = FeedsDidUKnowHomeBinding.inflate(
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
            inAppwebview1x1 -> {
                val mRowBinding = FeedsDidUKnowHome1x1Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1(mRowBinding)
            }
            staticImage1x1 -> {
                val mRowBinding = FeedsDidUKnowHome1x1Binding.inflate(
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

    inner class DiduKnowViewHolder(
        private val mRowItemBinding: FeedsDidUKnowHomeBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel


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

    inner class StaticImage1x1(
        private val mRowItemBinding: FeedsDidUKnowHome1x1Binding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: FeedsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = FeedsViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel


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
            AppConstants.FEED_TYPE_STATIC_IMAGE1x1 -> {
                staticImage1x1
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
        fun onFeedClick(position:Int,feedDetails: FeedDetails)
    }



}