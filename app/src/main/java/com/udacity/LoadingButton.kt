package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.udacity.utils.BORDER_STROKE_WIDTH
import com.udacity.utils.CIRCLE_DEGREE_DIFFERENCE
import com.udacity.utils.CIRCLE_SIZE_BIAS
import com.udacity.utils.CIRCLE_START_ANGLE
import com.udacity.utils.CIRCLE_X_BIAS
import com.udacity.utils.HALF
import com.udacity.utils.ONE
import com.udacity.utils.TEXT_SIZE
import com.udacity.utils.TEXT_X_BIAS
import com.udacity.utils.TEXT_Y_BIAS
import com.udacity.utils.TOTAL_PROGRESS
import com.udacity.utils.ZERO
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = ZERO
) : View(context, attrs, defStyleAttr) {

    // canvas dimensions, painter, & empty circle space
    private var canvasWidth = ZERO
    private var canvasHeight = ZERO
    private var canvasPainter = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circleSpace = RectF()

    // button custom attributes
    private val buttonBackgroundColor: Int
    private val buttonTextColor: Int
    private val buttonBorderColor: Int
    private val buttonProgressColor: Int
    private val circleProgressColor: Int

    // button state
    private var buttonState: ButtonState by Delegates.observable(ButtonState.UnClicked) { p, old, new -> }

    // animation values
    @Volatile
    private var circleProgress: Double = ZERO.toDouble()
    @Volatile
    private var buttonProgress: Double = ZERO.toDouble()
    private val buttonAnimation: ValueAnimator = AnimatorInflater.loadAnimator(
        context, R.animator.button_loading_animation
    ) as ValueAnimator
    private val buttonAnimListener = ValueAnimator.AnimatorUpdateListener {
        buttonProgress = (it.animatedValue as Float).toDouble()
        circleProgress = (buttonProgress * CIRCLE_DEGREE_DIFFERENCE)
        invalidateView()
    }

    // button text
    private lateinit var buttonText: String


    // initialize
    init {

        println("INIT")

        // read custom attributes for custom view
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            ZERO, ZERO
        ).apply {
            try {
                context.apply {
                    // button color - default value
                    buttonBackgroundColor = getInt(
                        R.styleable.LoadingButton_buttonBackgroundColor,
                        getColor(R.color.colorPrimary)
                    )

                    // button text color - default value
                    buttonTextColor = getInt(
                        R.styleable.LoadingButton_buttonTextColor,
                        Color.WHITE
                    )

                    // button border color - default value
                    buttonBorderColor = getInt(
                        R.styleable.LoadingButton_buttonBorderColor,
                        getColor(R.color.colorPrimaryDark)
                    )

                    // button progress color - default value
                    buttonProgressColor = getInt(
                        R.styleable.LoadingButton_buttonProgressColor,
                        getColor(R.color.colorSecondary)
                    )

                    // circle progress color - default value
                    circleProgressColor = getInt(
                        R.styleable.LoadingButton_circleProgressColor,
                        getColor(R.color.colorAccent)
                    )

                    // add button animator value listener
                    buttonAnimation.addUpdateListener(buttonAnimListener)
                }

            } finally {
                // recycle this typedArray shared resource - required
                recycle()
            }
        }
    }


    /**
     * updates button state and
     * begins animation.
     */
    fun startAnimation(): Boolean {
        buttonState = ButtonState.Loading
        buttonAnimation.start()
        return true
    }


    /**
     * updates button UI to
     * the assigned state
     * & calls reset value animator.
     */
    fun updateButtonUI(state: ButtonState) {
        buttonState = state
        resetValueAnimator()
    }


    /**
     * stops animation and updates
     * progress to show given state.
     * calls invalidateView after.
     */
    private fun resetValueAnimator() {
        buttonAnimation.cancel()
        buttonProgress = when(buttonState) {
            ButtonState.Completed -> TOTAL_PROGRESS.toDouble()
            else -> ZERO.toDouble()
        }
        invalidateView()
    }


    /**
     * updates any custom view property
     * changes and redraws UI to screen
     */
    private fun invalidateView() {
        invalidate()
        requestLayout()
    }


    /**
     *  calculates and updates the
     *  custom view's canvas width
     *  and height for onDraw
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        println("ON-MEASURE")

        // total minimum width of view including L/R padding
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth

        // view width after calculating against MeasureSpec
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, ONE)

        // view height after calculating against MeasureSpec
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            ZERO
        )

        // update width & height
        canvasWidth = w
        canvasHeight = h

        // store measured width & height for onDraw
        setMeasuredDimension(w, h)
    }


    /**
     *  draws on the custom view
     *  canvas with any given
     *  dimensions that are passed
     *  to the canvas
     */
    override fun onDraw(canvas: Canvas) {
        println("ON-DRAW")

        // paint button on canvas
        paintButton(canvas)

        // paint button border on canvas
        paintBorder(canvas)

        // paint button text on canvas w/ Circle
        paintText(canvas)
    }


    /**
     * updates any default painter values.
     */
    private fun updatePainter(
        painterColor: Int = Color.BLACK,
        painterStrokeWidth: Float = ZERO.toFloat(),
        painterStyle: Paint.Style = Paint.Style.FILL,
        painterTextAlign: Align = Align.CENTER,
        painterTextSize: Float = TEXT_SIZE.toFloat(),
        painterTypeface: Typeface = Typeface.DEFAULT_BOLD
    ) {
        canvasPainter.apply {
            color = painterColor
            strokeWidth = painterStrokeWidth
            style = painterStyle
            textAlign = painterTextAlign
            textSize = painterTextSize
            typeface = painterTypeface
        }
    }


    /**
     * paints button on canvas
     */
    private fun paintButton(canvas: Canvas) {
        println("PAINT-BUTTON")

        // update painter color
        updatePainter(buttonBackgroundColor)

        // draw button
        canvas.drawRect(
            ZERO.toFloat(),     // x-axis start
            ZERO.toFloat(),     // y-axis start
            width.toFloat(),    // x-axis end
            height.toFloat(),   // y-axis end
            canvasPainter       // painter
        )

        // update painter color
        updatePainter(buttonProgressColor)

        // draw download progress color
        canvas.drawRect(
            ZERO.toFloat(),                                         // x-axis start
            ZERO.toFloat(),                                         // y-axis start
            (width * (buttonProgress / TOTAL_PROGRESS)).toFloat(),  // x-axis end
            height.toFloat(),                                       // y-axis end
            canvasPainter                                           // painter
        )
    }


    /**
     * paints button border on canvas
     */
    private fun paintBorder(canvas: Canvas) {
        println("PAINT-BORDER")

        // update painter non-default attributes
        updatePainter(
            buttonBorderColor,
            BORDER_STROKE_WIDTH.toFloat(),
            Paint.Style.STROKE
        )

        // draw button border
        canvas.drawRect(
            ZERO.toFloat(),     // x-axis start
            ZERO.toFloat(),     // y-axis start
            width.toFloat(),    // x-axis end
            height.toFloat(),   // y-axis end
            canvasPainter       // painter
        )
    }


    /**
     * paints button text on canvas
     */
    private fun paintText(canvas: Canvas) {
        println("PAINT-TEXT")

        // assign & paint text
        when (buttonState) {
            ButtonState.Loading -> {    // button loading
                buttonText = resources.getString(R.string.toast_downloading)
                paintDownloading(canvas)
            }
            ButtonState.Completed -> {  // button completed
                buttonText = resources.getString(R.string.button_download_complete)
                paintCompleted(canvas)
            }
            else -> {                   // button unclicked
                buttonText = resources.getString(R.string.button_download_text)
                paintDefault(canvas)
            }
        }
    }


    /**
     * paints DOWNLOAD text
     */
    private fun paintDefault(canvas: Canvas) {
        // update painter color
        updatePainter(buttonTextColor)

        // draw default text
        canvas.drawText(
            context.getString(R.string.button_download_text),   // text
            (width / HALF).toFloat(),                           // x-axis alignment
            ((height + TEXT_Y_BIAS) / HALF).toFloat(),          // y-axis alignment
            canvasPainter                                       // painter
        )
    }


    /**
     * paints DOWNLOADING... text with Circle
     */
    private fun paintDownloading(canvas: Canvas) {
        // update painter color - Text
        updatePainter(buttonTextColor)

        // draw downloading text
        canvas.drawText(
            context.getString(R.string.toast_downloading),  // text
            (width / (HALF + TEXT_X_BIAS)).toFloat(),       // centerX
            ((height + TEXT_Y_BIAS) / HALF).toFloat(),      // centerY
            canvasPainter                                   // painter
        )

        // update painter color - Circle
        updatePainter(circleProgressColor)

        // allocate space for circle
        centerCircleSpace()

        // draw circle arc
        canvas.drawArc(
            circleSpace,
            CIRCLE_START_ANGLE.toFloat(),
            circleProgress.toFloat(),
            true,
            canvasPainter
        )
    }


    /**
     * centers & allocates empty space
     * for drawing circle on canvas.
     */
    private fun centerCircleSpace() {
        val size = ((TEXT_SIZE / HALF) + CIRCLE_SIZE_BIAS)
        val centerX = (width - CIRCLE_X_BIAS).toFloat()
        val centerY = (height / HALF).toFloat()

        circleSpace.set(
            centerX - size,
            centerY - size,
            centerX + size,
            centerY + size
        )
    }


    /**
     * paints DOWNLOAD COMPLETE text
     */
    private fun paintCompleted(canvas: Canvas) {
        // update painter color
        updatePainter(buttonTextColor)

        // draw download complete text
        canvas.drawText(
            context.getString(R.string.button_download_complete),   // text
            (width / HALF).toFloat(),                               // centerX
            ((height + TEXT_Y_BIAS) / HALF).toFloat(),              // centerY
            canvasPainter                                           // painter
        )
    }
}