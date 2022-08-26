package com.fypmoney.util.spinwheel

import android.animation.Animator
import android.animation.TimeInterpolator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.IntDef
import androidx.core.graphics.ColorUtils
import com.fypmoney.model.LuckyItem
import com.fypmoney.util.spinwheel.LuckyWheelUtils.drawableToBitmap
import com.fypmoney.util.spinwheel.PielView.SpinRotation.Companion.CLOCKWISE
import com.fypmoney.util.spinwheel.PielView.SpinRotation.Companion.COUNTERCLOCKWISE
import java.util.*

class PielView : View {
    private var mRange = RectF()
    private var mRadius = 0
    private var mArcPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mTopTextPadding = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mRoundOfNumber = 4
    private var mEdgeWidth = -1
    private var isRunning = false
    private var borderColor = 0
    private var defaultBackgroundColor = 0
    private var drawableCenterImage: Drawable? = null
    private var textColor = 0
    private var predeterminedNumber = -1
    var viewRotation = 0f
    var fingerRotation = 0.0
    var downPressTime: Long = 0
    var upPressTime: Long = 0
    var newRotationStore = DoubleArray(3)
    private var mLuckyItemList: List<LuckyItem>? = null
    private var mPieRotateListener: PieRotateListener? = null

    interface PieRotateListener {
        fun rotateDone(index: Int)
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setPieRotateListener(listener: PieRotateListener?) {
        mPieRotateListener = listener
    }

    private fun init() {
        mArcPaint = Paint()
        mArcPaint!!.isAntiAlias = true
        mArcPaint!!.isDither = true
        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        if (textColor != 0) mTextPaint!!.color = textColor
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14f,
            resources.displayMetrics
        )
        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
    }

    fun getLuckyItemListSize(): Int {
        return mLuckyItemList!!.size
    }

    fun setData(luckyItemList: List<LuckyItem>?) {
        mLuckyItemList = luckyItemList
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setBorderColor(color: Int) {
        borderColor = color
        invalidate()
    }

    fun setTopTextPadding(padding: Int) {
        mTopTextPadding = padding
        invalidate()
    }

    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setTopTextSize(size: Int) {
        mTopTextSize = size + 2
        invalidate()
    }

    fun setSecondaryTextSizeSize(size: Int) {
        mSecondaryTextSize = size
        invalidate()
    }

    fun setBorderWidth(width: Int) {
        mEdgeWidth = width
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    private fun drawPieBackgroundWithBitmap(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2, mPadding / 2,
                measuredWidth - mPadding / 2,
                measuredHeight - mPadding / 2
            ), null
        )
    }

    /**
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mLuckyItemList == null) {
            return
        }
        drawBackgroundColor(canvas, defaultBackgroundColor)
        init()
        var tmpAngle = mStartAngle
        val sweepAngle = 360f / mLuckyItemList!!.size
        for (i in mLuckyItemList!!.indices) {
            if (mLuckyItemList!![i].color != 0) {
                mArcPaint!!.style = Paint.Style.FILL
                mArcPaint!!.color = mLuckyItemList!![i].color
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }
            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint!!.style = Paint.Style.STROKE
                mArcPaint!!.color = borderColor
                mArcPaint!!.strokeWidth = mEdgeWidth.toFloat()
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }
            val sliceColor =
                if (mLuckyItemList!![i].color != 0) mLuckyItemList!![i].color else defaultBackgroundColor
            if (i != 0 && mLuckyItemList!![i].icon != 0) drawImage(
                canvas, tmpAngle, BitmapFactory.decodeResource(
                    resources,
                    mLuckyItemList!![i].icon
                )
            )
            if (i == 0 && mLuckyItemList!![i].icon != 0) drawImagesingle(
                canvas, tmpAngle, BitmapFactory.decodeResource(
                    resources,
                    mLuckyItemList!![i].icon
                )
            )
            if (!TextUtils.isEmpty(mLuckyItemList!![i].topText)) drawTopText(
                canvas,
                tmpAngle,
                sweepAngle,
                mLuckyItemList!![i].topText,
                sliceColor
            )
            if (!TextUtils.isEmpty(mLuckyItemList!![i].secondaryText)) drawSecondaryText(
                canvas,
                tmpAngle,
                mLuckyItemList!![i].secondaryText,
                sliceColor
            )
            tmpAngle += sweepAngle
        }
        drawCenterImage(canvas, drawableCenterImage)
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == 0) return
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = color
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            (mCenter - 5).toFloat(),
            mBackgroundPaint!!
        )
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = Math.min(measuredWidth, measuredHeight)
        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        setMeasuredDimension(width, width)
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param bitmap
     */
    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        canvas.save()
        val imgWidth = 60
        val angle = ((tmpAngle + 360f / mLuckyItemList!!.size / 2) * (Math.PI / 180)).toFloat()
        val x = +(mCenter + mRadius * 4 / 7 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius * 4 / 7 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth / 2, y - imgWidth / 3,
            x + imgWidth / 2, y + imgWidth / 3
        )
        val arraySize = mLuckyItemList!!.size
        val initFloat = tmpAngle + 360f / arraySize / 2 + 90
        canvas.rotate(initFloat + arraySize / 18f, x.toFloat(), y.toFloat())
        canvas.drawBitmap(bitmap, null, rect, null)
        canvas.restore()
    }

    private fun drawImagesingle(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        canvas.save()
        val imgWidth = 60
        val angle = ((tmpAngle + 360f / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = +(mCenter + mRadius * 3 / 5 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius * 3 / 5 / 2 * Math.sin(angle.toDouble())).toInt()

//        Rect rect = new Rect(x - imgWidth, y - imgWidth / 2,
//                x + imgWidth, y + imgWidth / 2);
        val rect = Rect(
            x - imgWidth, y - imgWidth / 2 - 60,
            x + imgWidth, y + imgWidth / 2 + 10
        )
        val arraySize = mLuckyItemList!!.size
        val initFloat = tmpAngle + 360f / arraySize / 2 + 90
        canvas.rotate(initFloat + arraySize / 18f, x.toFloat(), y.toFloat())
        canvas.drawBitmap(bitmap, null, rect, null)
        canvas.restore()
    }

    private fun drawCenterImage(canvas: Canvas, drawable: Drawable?) {
        var bitmap = drawableToBitmap(drawable!!)
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            false
        )
        canvas.drawBitmap(
            bitmap, (measuredWidth / 2 - bitmap.width / 2).toFloat(), (
                    measuredHeight / 2 - bitmap.height / 2).toFloat(), null
        )
    }

    private fun isColorDark(color: Int): Boolean {
        val colorValue = ColorUtils.calculateLuminance(color)
        val compareValue = 0.30
        return colorValue <= compareValue
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private fun drawTopText(
        canvas: Canvas,
        tmpAngle: Float,
        sweepAngle: Float,
        mStr: String,
        backgroundColor: Int
    ) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textAlign = Paint.Align.LEFT
        mTextPaint!!.textSize = mTopTextSize.toFloat()
        mTextPaint!!.letterSpacing = .1f
        mTextPaint!!.isLinearText = true
        mTextPaint!!.isSubpixelText = true
        var noOfLines = 0
        for (line in mStr.split("\n").toTypedArray()) {
            noOfLines++
        }
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mLuckyItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mTopTextPadding + 30
        canvas.drawTextOnPath(mStr, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint!!)
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param mStr
     * @param backgroundColor
     */
    private fun drawSecondaryText(
        canvas: Canvas,
        tmpAngle: Float,
        mStr: String,
        backgroundColor: Int
    ) {
        canvas.save()
        val arraySize = mLuckyItemList!!.size
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textSize = mSecondaryTextSize.toFloat()
        mTextPaint!!.textAlign = Paint.Align.LEFT
        val textWidth = mTextPaint!!.measureText(mStr)
        val initFloat = tmpAngle + 360f / arraySize / 2
        val angle = (initFloat * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = RectF(
            x + textWidth, y.toFloat(),
            x - textWidth, y.toFloat()
        )
        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        path.close()
        canvas.rotate(initFloat + arraySize / 18f, x.toFloat(), y.toFloat())
        canvas.drawTextOnPath(
            mStr,
            path,
            mTopTextPadding / 7f,
            mTextPaint!!.textSize / 2.75f,
            mTextPaint!!
        )
        canvas.restore()
    }

    /**
     * @return
     */
    private fun getAngleOfIndexTarget(index: Int): Float {
        return 360f / mLuckyItemList!!.size * index
    }

    /**
     * @param numberOfRound
     */
    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }

    fun setPredeterminedNumber(predeterminedNumber: Int) {
        this.predeterminedNumber = predeterminedNumber
    }

    fun rotateTo(index: Int) {
        val rand = Random()
        rotateTo(index, rand.nextInt() * 3 % 2, true)
    }

    /**
     * @param index
     * @param rotation,  spin orientation of the wheel if clockwise or counterclockwise
     * @param startSlow, either animates a slow start or an immediate turn based on the trigger
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun rotateTo(index: Int, @SpinRotation rotation: Int, startSlow: Boolean) {
        if (isRunning) {
            return
        }
        val rotationAssess = if (rotation <= 0) 1 else -1

        //If the staring position is already off 0 degrees, make an illusion that the rotation has smoothly been triggered.
        // But this inital animation will just reset the position of the circle to 0 degreees.
        if (getRotation() != 0.0f) {
            setRotation(getRotation() % 360f)
            val animationStart: TimeInterpolator =
                if (startSlow) AccelerateInterpolator() else LinearInterpolator()
            //The multiplier is to do a big rotation again if the position is already near 360.
            val multiplier: Float = if (getRotation() > 200f) 2F else 1.toFloat()
            animate()
                .setInterpolator(animationStart)
                .setDuration(300L)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        isRunning = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isRunning = false
                        setRotation(0f)
                        rotateTo(index, rotation, false)
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
                .rotation(360f * multiplier * rotationAssess)
                .start()
            return
        }

        // This addition of another round count for counterclockwise is to simulate the perception of the same number of spin
        // if you still need to reach the same outcome of a positive degrees rotation with the number of rounds reversed.
        if (rotationAssess < 0) mRoundOfNumber++
        val targetAngle =
            360f * mRoundOfNumber * rotationAssess + 270f - getAngleOfIndexTarget(index) - 360f / mLuckyItemList!!.size / 2
        animate()
            .setInterpolator(DecelerateInterpolator())
            .setDuration(mRoundOfNumber * 1000 + 900L)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    isRunning = false
                    setRotation(getRotation() % 360f)
                    if (mPieRotateListener != null) {
                        mPieRotateListener!!.rotateDone(index)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            .rotation(targetAngle)
            .start()
    }

    private var touchEnabled = false
    fun isTouchEnabled(): Boolean {
        return touchEnabled
    }

    fun setTouchEnabled(touchEnabled: Boolean) {
        this.touchEnabled = touchEnabled
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isRunning || !touchEnabled) {
            return false
        }
        val x = event.x
        val y = event.y
        val xc = width / 2.0f
        val yc = height / 2.0f
        val newFingerRotation: Double
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewRotation = (rotation + 360f) % 360f
                fingerRotation =
                    Math.toDegrees(Math.atan2((x - xc).toDouble(), (yc - y).toDouble()))
                downPressTime = event.eventTime
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                newFingerRotation =
                    Math.toDegrees(Math.atan2((x - xc).toDouble(), (yc - y).toDouble()))
                if (isRotationConsistent(newFingerRotation)) {
                    rotation = newRotationValue(viewRotation, fingerRotation, newFingerRotation)
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                newFingerRotation =
                    Math.toDegrees(Math.atan2((x - xc).toDouble(), (yc - y).toDouble()))
                var computedRotation =
                    newRotationValue(viewRotation, fingerRotation, newFingerRotation)
                fingerRotation = newFingerRotation

                // This computes if you're holding the tap for too long
                upPressTime = event.eventTime
                if (upPressTime - downPressTime > 700L) {
                    // Disregarding the touch since the tap is too slow
                    return true
                }

                // These operators are added so that fling difference can be evaluated
                // with usually numbers that are only around more or less 100 / -100.
                if (computedRotation <= -250f) {
                    computedRotation += 360f
                } else if (computedRotation >= 250f) {
                    computedRotation -= 360f
                }
                var flingDiff = (computedRotation - viewRotation).toDouble()
                if (flingDiff >= 200 || flingDiff <= -200) {
                    if (viewRotation <= -50f) {
                        viewRotation += 360f
                    } else if (viewRotation >= 50f) {
                        viewRotation -= 360f
                    }
                }
                flingDiff = (computedRotation - viewRotation).toDouble()
                if (flingDiff <= -60 ||  //If you have a very fast flick / swipe, you an disregard the touch difference
                    flingDiff < 0 && flingDiff >= -59 && upPressTime - downPressTime <= 200L
                ) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, SpinRotation.COUNTERCLOCKWISE, false)
                    } else {
                        rotateTo(getFallBackRandomIndex(), SpinRotation.COUNTERCLOCKWISE, false)
                    }
                }
                if (flingDiff >= 60 ||  //If you have a very fast flick / swipe, you an disregard the touch difference
                    flingDiff > 0 && flingDiff <= 59 && upPressTime - downPressTime <= 200L
                ) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, CLOCKWISE, false)
                    } else {
                        rotateTo(getFallBackRandomIndex(), CLOCKWISE, false)
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun newRotationValue(
        originalWheenRotation: Float,
        originalFingerRotation: Double,
        newFingerRotation: Double
    ): Float {
        val computationalRotation = newFingerRotation - originalFingerRotation
        return (originalWheenRotation + computationalRotation.toFloat() + 360f) % 360f
    }

    private fun getFallBackRandomIndex(): Int {
        val rand = Random()
        return rand.nextInt(mLuckyItemList!!.size - 1) + 0
    }

    /**
     * This detects if your finger movement is a result of an actual raw touch event of if it's from a view jitter.
     * This uses 3 events of rotation temporary storage so that differentiation between swapping touch events can be determined.
     *
     * @param newRotValue
     */
    private fun isRotationConsistent(newRotValue: Double): Boolean {
        if (java.lang.Double.compare(newRotationStore[2], newRotationStore[1]) != 0) {
            newRotationStore[2] = newRotationStore[1]
        }
        if (java.lang.Double.compare(newRotationStore[1], newRotationStore[0]) != 0) {
            newRotationStore[1] = newRotationStore[0]
        }
        newRotationStore[0] = newRotValue
        return !(java.lang.Double.compare(
            newRotationStore[2],
            newRotationStore[0]
        ) == 0 || java.lang.Double.compare(
            newRotationStore[1],
            newRotationStore[0]
        ) == 0 || java.lang.Double.compare(
            newRotationStore[2],
            newRotationStore[1]
        ) == 0 //Is the middle event the odd one out
                || newRotationStore[0] > newRotationStore[1] && newRotationStore[1] < newRotationStore[2]
                || newRotationStore[0] < newRotationStore[1] && newRotationStore[1] > newRotationStore[2])
    }

    @IntDef(CLOCKWISE, COUNTERCLOCKWISE)
    internal annotation class SpinRotation {
        companion object {
            const val CLOCKWISE = 0
            const val COUNTERCLOCKWISE = 1
        }
    }
}