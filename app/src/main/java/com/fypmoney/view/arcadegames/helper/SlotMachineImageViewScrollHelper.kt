package com.fypmoney.view.arcadegames.helper

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.fypmoney.R
import com.fypmoney.databinding.ViewSlotMachineImageScrollingBinding
import com.fypmoney.view.arcadegames.SlotMachineImageEventEnd
import com.fypmoney.view.arcadegames.helper.Utils.NO_OF_IMAGES
import kotlinx.android.synthetic.main.view_slot_machine_image_scrolling.view.*

class SlotMachineImageViewScrollHelper(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    lateinit var eventEnd: SlotMachineImageEventEnd
    var lastResult = 0
    var oldValue = 0

    private var binding: ViewSlotMachineImageScrollingBinding =
        ViewSlotMachineImageScrollingBinding.inflate(LayoutInflater.from(context))

    companion object {
        private const val ANIMATION_DURATION = 100
    }

    val value: Int
        get() = Integer.parseInt(nextImage.tag.toString())

    @JvmName("setEventEnd1")
    fun setEventEnd(eventEnd: SlotMachineImageEventEnd) {
        this.eventEnd = eventEnd
    }


    init {
        addView(binding.root)
        nextImage.translationY = height.toFloat()
    }

    fun setValueRandom(image: Int, num_rotate: Int) {
        currentImage.animate()
            .translationY((-height).toFloat())
            .setInterpolator(AccelerateInterpolator())
            .setDuration(ANIMATION_DURATION.toLong()).start()

        nextImage.translationY = nextImage.height.toFloat()

        nextImage.animate()
            .translationY(0f)
            .setDuration(ANIMATION_DURATION.toLong())
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    setImage(currentImage, oldValue % NO_OF_IMAGES)
                    currentImage.translationY = 0f

                    if (oldValue != num_rotate) {//if still have rotate
                        setValueRandom(image, num_rotate)
                        oldValue++
                    } else {
                        lastResult = 0
                        oldValue = 0
                        setImage(nextImage, image % NO_OF_IMAGES)
                        eventEnd.eventEnd(image % NO_OF_IMAGES, currentImage)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            }).start()

        nextImage.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )

    }

    private fun setImage(img: ImageView?, value: Int) {

        when (value) {
            Utils.seven -> img!!.setImageResource(R.drawable.seven)
            Utils.diamond -> img!!.setImageResource(R.drawable.diamond)
            Utils.crown -> img!!.setImageResource(R.drawable.crown)
            Utils.apple -> img!!.setImageResource(R.drawable.apple)
            Utils.card -> img!!.setImageResource(R.drawable.card)
            Utils.grapes -> img!!.setImageResource(R.drawable.grapes)
        }

        img!!.tag = value
        lastResult = value
    }

}