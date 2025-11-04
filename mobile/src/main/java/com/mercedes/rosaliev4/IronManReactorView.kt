package com.mercedes.rosaliev4

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class IronManReactorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rotation = 0f
    private var glowAlpha = 255f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private var animatorRotation: ValueAnimator? = null
    private var animatorGlow: ValueAnimator? = null

    init {
        startAnimation()
    }

    private fun startAnimation() {
        // Rotation animation
        animatorRotation = ObjectAnimator.ofFloat(0f, 360f).apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                rotation = it.animatedValue as Float
                invalidate()
            }
            start()
        }

        // Glow pulse animation
        animatorGlow = ValueAnimator.ofFloat(100f, 200f, 100f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                glowAlpha = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = minOf(width, height) / 2f - 20

        // Draw rotating outer rings
        drawOuterRings(canvas, centerX, centerY, maxRadius)
        
        // Draw inner glowing core
        drawCore(canvas, centerX, centerY, maxRadius)
        
        // Draw Mercedes logo in center
        drawMercedesLogo(canvas, centerX, centerY)
    }

    private fun drawOuterRings(canvas: Canvas, cx: Float, cy: Float, maxRadius: Float) {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        // Outer glow ring
        paint.color = Color.parseColor("#FFD700")
        paint.alpha = (glowAlpha * 0.3).toInt()
        paint.strokeWidth = 3f
        canvas.drawCircle(cx, cy, maxRadius * 0.95f, paint)

        // Middle ring with rotation
        canvas.save()
        canvas.rotate(rotation, cx, cy)
        paint.color = Color.parseColor("#FFA500")
        paint.alpha = 180
        paint.strokeWidth = 2.5f
        canvas.drawCircle(cx, cy, maxRadius * 0.75f, paint)

        // Draw radial segments
        for (i in 0..2) {
            val angle = (i * 120) * Math.PI / 180
            val startX = cx + (maxRadius * 0.4 * cos(angle)).toFloat()
            val startY = cy + (maxRadius * 0.4 * sin(angle)).toFloat()
            val endX = cx + (maxRadius * 0.75 * cos(angle)).toFloat()
            val endY = cy + (maxRadius * 0.75 * sin(angle)).toFloat()
            canvas.drawLine(startX, startY, endX, endY, paint)
        }
        canvas.restore()

        // Inner ring
        paint.color = Color.parseColor("#FF6B00")
        paint.strokeWidth = 3f
        canvas.drawCircle(cx, cy, maxRadius * 0.55f, paint)
    }

    private fun drawCore(canvas: Canvas, cx: Float, cy: Float, maxRadius: Float) {
        paint.style = Paint.Style.FILL
        val coreRadius = maxRadius * 0.40f

        // Outer glow
        paint.color = Color.parseColor("#FFE135")
        paint.alpha = (glowAlpha * 0.5).toInt()
        canvas.drawCircle(cx, cy, coreRadius * 1.2f, paint)

        // Core circle
        paint.color = Color.parseColor("#FFFF99")
        paint.alpha = 220
        canvas.drawCircle(cx, cy, coreRadius, paint)

        // Bright center
        paint.color = Color.parseColor("#FFFFFF")
        paint.alpha = 180
        canvas.drawCircle(cx, cy, coreRadius * 0.6f, paint)
    }

    private fun drawMercedesLogo(canvas: Canvas, cx: Float, cy: Float) {
        val logoRadius = 30f
        
        // White circle background
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.alpha = 220
        canvas.drawCircle(cx, cy, logoRadius, paint)

        // Circle outline
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#00008B")
        paint.strokeWidth = 2f
        paint.alpha = 255
        canvas.drawCircle(cx, cy, logoRadius - 2, paint)

        // Mercedes three-pointed star
        paint.strokeWidth = 2.5f
        paint.strokeCap = Paint.Cap.ROUND

        // Top point (vertical)
        canvas.drawLine(cx, cy - 12, cx, cy - 5, paint)

        // Bottom left point
        canvas.drawLine(cx - 10, cy + 10, cx - 15, cy + 15, paint)

        // Bottom right point
        canvas.drawLine(cx + 10, cy + 10, cx + 15, cy + 15, paint)

        // Center dot
        paint.style = Paint.Style.FILL
        canvas.drawCircle(cx, cy, 2f, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorRotation?.cancel()
        animatorGlow?.cancel()
    }
}
