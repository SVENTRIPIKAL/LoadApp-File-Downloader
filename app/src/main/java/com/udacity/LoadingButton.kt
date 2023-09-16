package com.udacity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.udacity.utils.BORDER_STROKE_WIDTH
import com.udacity.utils.HALF
import com.udacity.utils.ONE
import com.udacity.utils.TEXT_AXIS_BIAS
import com.udacity.utils.TEXT_SIZE
import com.udacity.utils.ZERO
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // canvas painter
    private var painter = Paint(Paint.ANTI_ALIAS_FLAG)

    // button dimensions
    private var widthSize = ZERO
    private var heightSize = ZERO

    // button custom attributes
    private val buttonTextColor: Int
    private val buttonBackgroundColor: Int
    private val buttonBorderColor: Int
    private val buttonCircleColor: Int

    // button state
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

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
                    // assign button color - default value
                    buttonBackgroundColor = getInt(
                        R.styleable.LoadingButton_buttonBackgroundColor,
                        getColor(R.color.colorPrimary)
                    )

                    // assign button text color - default value
                    buttonTextColor = getInt(
                        R.styleable.LoadingButton_buttonTextColor,
                        getColor(R.color.white)
                    )

                    // assign button border color - default value
                    buttonBorderColor = getInt(
                        R.styleable.LoadingButton_buttonBorderColor,
                        getColor(R.color.colorPrimaryDark)
                    )

                    // assign button circle color - default value
                    buttonCircleColor = getInt(
                        R.styleable.LoadingButton_buttonCirlceColor,
                        getColor(R.color.colorAccent)
                    )
                }

            } finally {
                // recycle this typedArray shared resource - required
                recycle()
            }
        }
    }


    /**
     * update any custom view property
     * changes and redraw UI to screen
     */
    private fun invalidateView() {
        invalidate()
        requestLayout()
    }


    /**
     *  function that calculates and
     *  updates the custom view's
     *  width and height for onDraw
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
        widthSize = w
        heightSize = h

        // store measured width & height for onDraw
        setMeasuredDimension(w, h)
    }


    /**
     *  function that draws on the
     *  custom view with any given
     *  dimensions that are passed
     *  to the canvas
     */
    override fun onDraw(canvas: Canvas) {
        println("ON-DRAW")

        // paint button on canvas
        paintButton(canvas)

        // paint button border on canvas
        paintBorder(canvas)

        // paint button text on canvas
        paintText(canvas)
    }


    /**
     * updates any default painter values.
     * text size & stroke width attributes
     * need their values to be explicitly
     * assigned.
     */
    private fun updatePainter(
        painterColor: Int = Color.BLACK,
        painterStrokeWidth: Float = ZERO.toFloat(),
        painterStyle: Paint.Style = Paint.Style.FILL,
        painterTextAlign: Align = Align.CENTER,
        painterTextSize: Float = TEXT_SIZE,
        painterTypeface: Typeface = Typeface.DEFAULT_BOLD
    ) {
        painter.apply {
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

        // update painter non-default attributes
        updatePainter(buttonBackgroundColor)

        // draw button
        canvas.drawRect(
            ZERO.toFloat(),     // x-axis start
            ZERO.toFloat(),     // y-axis start
            width.toFloat(),    // x-axis end
            height.toFloat(),   // y-axis end
            painter             // painter
        )
    }


    /**
     * paints button border on canvas
     */
    private fun paintBorder(canvas: Canvas) {
        println("PAINT-BORDER")

        // update painter non-default attributes
        updatePainter(
            painterColor = context.getColor(R.color.colorPrimaryDark),
            painterStrokeWidth = BORDER_STROKE_WIDTH,
            painterStyle = Paint.Style.STROKE
        )

        // draw button border
        canvas.drawRect(
            ZERO.toFloat(),     // x-axis start
            ZERO.toFloat(),     // y-axis start
            width.toFloat(),    // x-axis end
            height.toFloat(),   // y-axis end
            painter             // painter
        )
    }


    /**
     * paints button text on canvas
     */
    private fun paintText(canvas: Canvas) {
        println("PAINT-TEXT")

        // assign text
        buttonText = when (buttonState) {
            ButtonState.Completed -> resources.getString(R.string.button_download_text)
            else -> resources.getString(R.string.toast_downloading)
        }

        // update painter non-default attributes
        updatePainter(buttonTextColor)

        // draw text
        canvas.drawText(
            buttonText,                                     // text
            ((width) / HALF).toFloat(),                     // x-axis alignment
            ((height + TEXT_AXIS_BIAS) / HALF).toFloat(),   // y-axis alignment
            painter                                         // painter
        )
    }
}