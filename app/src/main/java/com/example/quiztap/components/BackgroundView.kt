package com.example.quiztap.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.addListener
import androidx.core.util.toRange
import kotlin.math.roundToInt
import kotlin.random.Random

class BackgroundView: View {

    private var shapes = arrayListOf<Shape>()
    private var shapeCount = 5
    private val colors = arrayListOf(
    "#FF6B6B", // Soft Red
    "#FFD93D", // Bright Yellow
    "#6BCB77", // Fresh Green
    "#4D96FF", // Vivid Blue
    "#843BFF", // Electric Purple
    "#FF9A8B", // Peach Pink
    "#FF6A88", // Watermelon Red
    "#FF99AC", // Soft Rose
    "#36D1DC", // Aqua Blue
    "#5B86E5", // Sky Indigo
    "#00F5D4", // Neon Aqua
    "#F15BB5", // Neon Pink
    "#9B5DE5", // Neon Purple
    "#FEE440", // Neon Yellow
    "#00BBF9", // Neon Blue
    "#A8DADC", // Aqua Mint
    "#F4A261", // Soft Orange
    "#2A9D8F", // Teal
    "#E9C46A", // Sand Yellow
    "#264653"  // Deep Navy
    )


    init {
        clipToOutline = true
    }

    private fun constructShapes(viewWidth: Int, viewHeight: Int) {
        val colors = colors.shuffled()
        shapes.clear()
        val xDif = (viewWidth * 0.2).roundToInt()
        val yDif = (viewHeight * 0.2).roundToInt()
        for (i in 0..shapeCount) {
            val x1 = (-xDif..xDif).random()
            val x2 = (viewWidth - xDif..viewWidth + xDif).random()
            val x = listOf(x1, x2).random().toFloat()

            val y1 = (-yDif..yDif).random()
            val y2 = (viewHeight - yDif..viewHeight + yDif).random()
            val y = listOf(y1, y2).random().toFloat()

            val color = colors.getOrNull(i) ?: colors.random()
            val radius = ((viewWidth * 0.4).roundToInt()..(viewWidth * 1f).roundToInt()).random()
            shapes.add(
                Shape(
                    position = PointF(x, y),
                    radius = radius.toFloat(),
                    color = color
                )
            )
        }

        invalidate()
    }


    fun animateShapes() {
        val animator = object : Runnable {
            override fun run() {
                assignDxDy()
                val animator2 = ValueAnimator.ofInt(0,100)
                animator2.duration = ANIMATION_DURATION
                animator2.addUpdateListener {
                    val value = it.animatedValue as Int
                    val percentage = 1- (value.toFloat()/100f)
                    shapes.forEach {
                        val newX = it.position.x + it.dx * percentage
                        val newY = it.position.y + it.dy * percentage
                        val newRadius = it.radius + it.dr * percentage
                        it.position.set(newX, newY)
                        it.radius = newRadius
                    }
                    invalidate()
                }
                animator2.start()
                animator2.addListener(onEnd = {
                    post(this)
                })
            }
        }
        postDelayed(animator, ANIMATION_DELAY)
    }

    private fun assignDxDy() {
        shapes.forEach {
            fun getRandom() = Random.nextDouble(-1.0,1.0).toFloat()
            it.dx = getRandom()
            it.dy = getRandom()
            it.dr = getRandom()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        shapes.forEach {
            canvas.drawCircle(it.position.x, it.position.y,it.radius, it.paint )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        constructShapes(w,h)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    data class Shape(
        val position: PointF,
        var radius: Float,
        val color: String,
    ) {
        val paint = Paint().apply {
            color = Color.parseColor(this@Shape.color)
            alpha = 50
        }

        var dx = 0f
        var dy = 0f
        var dr = 0f
    }

    companion object {
        private const val ANIMATION_DELAY = 5000L
        private const val ANIMATION_DURATION = 5000L
    }
}