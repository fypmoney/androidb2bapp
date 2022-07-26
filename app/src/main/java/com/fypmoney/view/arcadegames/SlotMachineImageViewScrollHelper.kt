package com.fypmoney.view.arcadegames

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.fypmoney.R
import com.fypmoney.databinding.ViewSlotMachineImageScrollingBinding
import com.fypmoney.view.arcadegames.helper.Common.NO_OF_IMAGES
import com.fypmoney.view.arcadegames.helper.Utils
import kotlinx.android.synthetic.main.view_slot_machine_image_scrolling.view.*

class SlotMachineImageViewScrollHelper(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    lateinit var eventEnd: SlotMachineImageEventEnd
    var lastResult = 0
    var oldValue = 0

    private var binding: ViewSlotMachineImageScrollingBinding =
        ViewSlotMachineImageScrollingBinding.inflate(LayoutInflater.from(context))

    companion object {
        private const val ANIMATION_DURATION = 120
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
            .setDuration(ANIMATION_DURATION.toLong()).start()

        nextImage.translationY = nextImage.height.toFloat()

        nextImage.animate().translationY(0f).setDuration(ANIMATION_DURATION.toLong())
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
                        setImage(nextImage, image)
                        eventEnd.eventEnd(image % NO_OF_IMAGES, currentImage)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            }).start()
    }

    private fun setImage(img: ImageView?, value: Int) {

        when (value) {
            Utils.mynts -> img!!.setImageResource(R.drawable.mynts_new)
            Utils.tickets -> img!!.setImageResource(R.drawable.ticket)
            Utils.cash -> img!!.setImageResource(R.drawable.cash)
        }

        img!!.tag = value
        lastResult = value
    }

}