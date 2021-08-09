package com.fypmoney.view.fypstories.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.fypmoney.R
import java.util.*


class FypStoriesView : LinearLayout {
    private val PROGRESS_BAR_LAYOUT_PARAM = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f)
    private val SPACE_LAYOUT_PARAM = LayoutParams(5, LayoutParams.WRAP_CONTENT)
    private val progressBars: MutableList<FypStoriesProgressBar> = ArrayList<FypStoriesProgressBar>()
    private var storiesCount = -1

    /**
     * pointer of running animation
     */
    private var current = -1
    private var storiesListener: StoriesListener? = null
    var isComplete = false
    private var isSkipStart = false
    private var isReverseStart = false

    interface StoriesListener {
        fun onNext()
        fun onPrev()
        fun onComplete()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = HORIZONTAL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FypStoriesView)
        storiesCount = typedArray.getInt(R.styleable.FypStoriesView_progressCount, 0)
        typedArray.recycle()
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()
        for (i in 0 until storiesCount) {
            val p: FypStoriesProgressBar = createProgressBar()
            progressBars.add(p)
            addView(p)
            if (i + 1 < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): FypStoriesProgressBar {
        val p = FypStoriesProgressBar(context)
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM)
        return p
    }

    private fun createSpace(): View {
        val v = View(context)
        v.layoutParams = SPACE_LAYOUT_PARAM
        return v
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    fun setStoriesCount(storiesCount: Int) {
        this.storiesCount = storiesCount
        bindViews()
    }

    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    fun setStoriesListener(storiesListener: StoriesListener?) {
        this.storiesListener = storiesListener
    }

    /**
     * Skip current story
     */
    fun skip() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return
        val p: FypStoriesProgressBar = progressBars[current]
        isSkipStart = true
        p.setMax()
    }

    /**
     * Reverse current story
     */
    fun reverse() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return
        val p: FypStoriesProgressBar = progressBars[current]
        isReverseStart = true
        p.setMin()
    }

    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    fun setStoryDuration(duration: Long) {
        for (i in progressBars.indices) {
            progressBars[i].setDuration(duration)
            progressBars[i].setCallback(callback(i))
        }
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    fun setStoriesCountWithDurations(durations: LongArray) {
        storiesCount = durations.size
        bindViews()
        for (i in progressBars.indices) {
            progressBars[i].setDuration(durations[i])
            progressBars[i].setCallback(callback(i))
        }
    }

    private fun callback(index: Int): FypStoriesProgressBar.Callback {
        return object : FypStoriesProgressBar.Callback {
            override fun onStartProgress() {
                current = index
            }

            override fun onFinishProgress() {
                if (isReverseStart) {
                    if (storiesListener != null) storiesListener!!.onPrev()
                    if (0 <= current - 1) {
                        val p: FypStoriesProgressBar = progressBars[current - 1]
                        p.setMinWithoutCallback()
                        progressBars[--current].startProgress()
                    } else {
                        progressBars[current].startProgress()
                    }
                    isReverseStart = false
                    return
                }
                val next = current + 1
                if (next <= progressBars.size - 1) {
                    if (storiesListener != null) storiesListener!!.onNext()
                    progressBars[next].startProgress()
                } else {
                    isComplete = true
                    if (storiesListener != null) storiesListener!!.onComplete()
                }
                isSkipStart = false
            }
        }
    }

    /**
     * Start progress animation
     */
    fun startStories() {
        progressBars[0].startProgress()
    }

    /**
     * Start progress animation from specific progress
     */
    fun startStories(from: Int) {
        for (i in 0 until from) {
            progressBars[i].setMaxWithoutCallback()
        }
        progressBars[from].startProgress()
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    fun destroy() {
        for (p in progressBars) {
            p.clear()
        }
    }

    /**
     * Pause story
     */
    fun pause() {
        if (current < 0) return
        progressBars[current].pauseProgress()
    }

    /**
     * Resume story
     */
    fun resume() {
        if (current < 0) return
        progressBars[current].resumeProgress()
    }

    companion object {
        private val TAG = FypStoriesView::class.java.simpleName
    }
}
