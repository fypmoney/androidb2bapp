package com.fypmoney.view.fypstories.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fypmoney.bindingAdapters.loadImage
import androidx.fragment.app.DialogFragment
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomsheetStoriesBinding
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.graphics.drawable.Drawable
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat

import com.bumptech.glide.load.engine.GlideException

import com.bumptech.glide.request.RequestListener

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource


class StoriesBottomSheet(var resourceList: List<String?>):
    BottomSheetDialogFragment(), FypStoriesView.StoriesListener {
    private var binding : BottomsheetStoriesBinding? = null
    private lateinit var bottomSheetDialog : BottomSheetDialog
    private val durations = longArrayOf(
        500L, 1000L, 1500L, 4000L, 5000L, 1000
    )
    private val PROGRESS_COUNT = resourceList.size

    var pressTime = 0L
    var limit = 500L
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_stories,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.stories.setStoriesCount(PROGRESS_COUNT)
        binding!!.stories.setStoryDuration(3000L)

        binding!!.stories.setStoriesListener(this)
        binding!!.stories.startStories(counter)

        loadImage(view = binding!!.storiesIv,imageUrl = resourceList[counter] ,
            ContextCompat.getDrawable(binding!!.storiesIv.context,R.drawable.progress_bar_drawable),
            rounded = false
        )

        binding!!.reverse.setOnClickListener { binding!!.stories.reverse() }
        binding!!.reverse.setOnTouchListener(onTouchListener)

        // bind skip view

        // bind skip view
        binding!!.skip.setOnClickListener { binding!!.stories.skip() }
        binding!!.skip.setOnTouchListener(onTouchListener)
    }

    private val onTouchListener = OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                binding!!.stories.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                binding!!.stories.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onNext() {
        loadImage(view = binding!!.storiesIv,imageUrl = resourceList[++counter],
            ContextCompat.getDrawable(binding!!.storiesIv.context,R.drawable.progress_bar_drawable),
            rounded = false
        )

    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        loadImage(view = binding!!.storiesIv,imageUrl = resourceList[--counter],
            ContextCompat.getDrawable(binding!!.storiesIv.context,R.drawable.progress_bar_drawable),
            rounded = false
        )


    }

    override fun onComplete() {
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.stories.destroy()
    }
}