package com.fypmoney.view.home.main.explore.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.*
import com.fypmoney.util.AppConstants
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.view.ExploreViewHelper
import com.fypmoney.view.home.main.explore.viewmodel.ExploreFragmentVM
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


/**
 * This adapter class is used to handle feeds
 */
class ExploreAdapter(
    var viewModel: ExploreFragmentVM,
    var onFeedItemClickListener: OnFeedItemClickListener,
    val feedList: List<SectionContentItem?>?
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
                val mRowBinding = DynamicCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return DiduKnowViewHolder(mRowBinding)
            }
            staticImage1x1 -> {
                val mRowBinding = ExploreDidYouKnowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1(mRowBinding)
            }
            staticImage1x1_5 -> {
                val mRowBinding = ExploreStaticImageAspectRatioBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1_5(mRowBinding)
            }
            inAppwebview1x1 -> {
                val mRowBinding = ExploreDidYouKnowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return StaticImage1x1(mRowBinding)
            }

            typeWithTitle -> {
                val mRowBinding = ExploreRowLayoutBinding.inflate(
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
        return when (feedList?.get(position)?.contentType) {
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
        when (feedList?.get(position)?.contentType) {
            AppConstants.FEED_TYPE_BLOG -> {

            }
        }
        return holder.onBind(position)
    }


    inner class StaticImage1x1_5(
        private val mRowItemBinding: ExploreStaticImageAspectRatioBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: ExploreViewHelper
        override fun onBind(position: Int) {
            mViewHelper = ExploreViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper



            mRowItemBinding.executePendingBindings()

        }
    }

    inner class ViewHolder(
        private val mRowItemBinding: ExploreRowLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: ExploreViewHelper
        override fun onBind(position: Int) {
            mViewHelper = ExploreViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel




            mRowItemBinding.executePendingBindings()

        }


    }


    inner class DiduKnowViewHolder(
        private val mRowItemBinding: DynamicCardBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: ExploreViewHelper
        override fun onBind(position: Int) {
            mViewHelper = ExploreViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel


            val set = ConstraintSet()
            set.clone(mRowItemBinding.layout)
            set.setDimensionRatio(mRowItemBinding.image.id, "4:5")
            set.applyTo(mRowItemBinding.layout)


            mRowItemBinding.executePendingBindings()

        }


    }

    inner class StaticImage1x1(
        private val mRowItemBinding: ExploreDidYouKnowBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: ExploreViewHelper
        override fun onBind(position: Int) {
            mViewHelper = ExploreViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel



            mRowItemBinding.executePendingBindings()

        }


    }


    inner class VideoViewHolder(
        private val mRowItemBinding: FeedRowLayoutVideoviewBinding? = null,
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: ExploreViewHelper
        override fun onBind(position: Int) {
            mViewHelper = ExploreViewHelper(
                position,
                feedList?.get(position), onFeedItemClickListener
            )
            mRowItemBinding?.youtubePlayerView?.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    try {
                        youTubePlayer.cueVideo(
                            feedList?.get(position)?.contentResourceUri!!, 0f
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
        fun onFeedClick(position: Int, feedDetails: SectionContentItem)
    }

}