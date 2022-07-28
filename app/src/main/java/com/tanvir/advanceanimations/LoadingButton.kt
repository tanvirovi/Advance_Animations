package com.tanvir.advanceanimations

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.BLACK // default color
    private val ovalSpace = RectF()
    // tells the compiler that the value of a variable
    // must never be cached as its value may change outside
    @Volatile
    private var progress: Double = 0.0
    private var valueAnimator: ValueAnimator

    // observes the state of button
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
    }

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        invalidate()    // redraw the screen
        requestLayout() // when rectangular progress dimension changes
    }

    private fun setSpace() {
        val horizontalCenter = (width.div(1.3)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val ovalSize = 50
        ovalSpace.set(
            horizontalCenter - ovalSize,
            verticalCenter - ovalSize,
            horizontalCenter + ovalSize,
            verticalCenter + ovalSize
        )
    }

    // call after downloading is completed
    fun hasFailedDownload() {
        // cancel the animation when file is downloaded
        buttonState = ButtonState.Clicked
        valueAnimator.cancel()
    }
    fun hasCompletedDownload() {
        valueAnimator.cancel()
        valueAnimator.setDuration(300)
    }
    fun nothingIsSelected() {
        // cancel the animation when file is downloaded
        buttonState = ButtonState.Clicked
        valueAnimator.cancel()
    }


    // initialize
    init {
        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            context,
            // properties for downloading progress is defined
            R.animator.loading_animation
        ) as ValueAnimator

        valueAnimator.addUpdateListener(updateListener)
        valueAnimator.doOnStart {

            isEnabled = false
        }
        valueAnimator.doOnEnd {
            buttonState = ButtonState.Completed
            isEnabled = true
            valueAnimator.cancel()

            invalidate()
            requestLayout()
        }

        // initialize custom attributes of the button
        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        try {

            // button back-ground color
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )

            // button text color
            textColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.white)
            )
        } finally {
            // clearing all the data associated with attribute
            attr.recycle()
        }
    }

    // set attributes of paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER // button text alignment
        textSize = 55.0f //button text size
        typeface = Typeface.create("", Typeface.BOLD) // button text's font style
    }

    private val fillArcPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = Color.WHITE
    }

    override fun performClick(): Boolean {
        super.performClick()

        if (buttonState == ButtonState.Completed) {
            buttonState = ButtonState.Loading
            animation()
        }else{
            buttonState = ButtonState.Completed
            return true
        }
        return true
    }

    // start the animation when button is clicked
    private fun animation() {
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 0f
        paint.color = bgColor
        // draw custom button
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // to show rectangular progress on custom button while file is downloading
        if (buttonState == ButtonState.Loading) {
            paint.color = Color.parseColor("#004349")
            canvas.drawRect(
                0f, 0f,
                (width * (progress / 100)).toFloat(), height.toFloat(), paint
            )
        }

        if (buttonState == ButtonState.Loading){
            paint.color = Color.YELLOW
            setSpace()
            val percentageToFill = 360*(progress/100)
            canvas.drawArc(ovalSpace, 0f, percentageToFill.toFloat(), true, fillArcPaint)
        }

        // check the button state
        val buttonText = if (buttonState == ButtonState.Loading)
            resources.getString(R.string.button_loading)  // We are loading as button text
        else resources.getString(R.string.button_download)// download as button text

        // write the text on custom button
        paint.color = textColor
        canvas.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)

    }
}