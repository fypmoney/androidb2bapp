package com.fypmoney.view.customview

import android.content.Context
import android.widget.FrameLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.content.res.TypedArray
import com.fypmoney.R
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import android.graphics.drawable.Drawable
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuff
import android.view.MotionEvent
import android.view.KeyEvent
import com.fypmoney.databinding.BtnPrimaryBinding

class PrimaryButton : FrameLayout {
    private var listener: OnClickListener? = null
    private var enabled = false
    private var text: String? = null
    private var binding: BtnPrimaryBinding? = null
    private var enabledBackgroundColor: Int = ContextCompat.getColor(context, R.color.colorPrimary)
    private var disabledBackgroundColor: Int = ContextCompat.getColor(context, R.color.buttonUnselectedColor)
    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = BtnPrimaryBinding.inflate(inflater, this, true)
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.PrimaryButton, 0, 0
        )
        enabled = a.getBoolean(R.styleable.PrimaryButton_enabled, true)
        text = a.getString(R.styleable.PrimaryButton_text)

        enabledBackgroundColor = a.getColor(R.styleable.PrimaryButton_enabled_bg,
            ContextCompat.getColor(context, R.color.colorPrimary
        ))
        disabledBackgroundColor = a.getColor(R.styleable.PrimaryButton_disabled_bg,
            ContextCompat.getColor(context, R.color.colorPrimary
        ))
        // make button enable / disable
        binding!!.primaryButton.isEnabled = enabled

        // keep button text
        binding!!.primaryButton.text = text
        applyStyle()
    }

    fun setText(text: String?) {
        if (!TextUtils.isEmpty(text)) {
            binding!!.primaryButton.text = text
        }
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        applyStyle()
        postInvalidate()
    }

    /*fun setStroked() {
        binding!!.primaryButton.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.duk_primary_c2
            )
        )
        binding!!.primaryButton.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        binding!!.primaryButton.setStrokeColor(
            ContextCompat.getColorStateList(
                context,
                R.color.duk_primary_c2
            )
        )
        binding!!.primaryButton.strokeWidth = AppUtils.dpToPx(1)
    }*/

    fun setLeftDrawable(drawable: Int, color: Int) {
        binding!!.primaryButton.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
        setTextViewDrawableColor(binding!!.primaryButton, color)
    }

    private fun setTextViewDrawableColor(textView: MaterialButton, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(textView.context, color),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun applyStyle() {
        binding!!.primaryButton.isEnabled = enabled
        if (enabled) {
            binding!!.primaryButton.setBackgroundColor(
                enabledBackgroundColor
            )
        } else {
            binding!!.primaryButton.setBackgroundColor(
                disabledBackgroundColor
            )
        }
    }

    // toggling progress loader
    fun setBusy(isBusy: Boolean) {
        binding!!.progressBar.visibility =
            if (isBusy) VISIBLE else GONE
        binding!!.primaryButton.visibility = if (isBusy) GONE else VISIBLE
        isEnabled = !isBusy
    }

    // Making default click listener work on custom view
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (listener != null && binding!!.primaryButton.isEnabled) listener!!.onClick(this)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP
            && (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                    || event.keyCode == KeyEvent.KEYCODE_ENTER)
        ) {
            if (listener != null) listener!!.onClick(this)
        }
        return super.dispatchKeyEvent(event)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }
}