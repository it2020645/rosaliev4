package com.mercedes.rosaliev4

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class IronManReactorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Animation properties
    private var rotation = 0f
    private var pulseRadius = 0f
    private var glowIntensity = 1f
    private var sparklePhase = 0f
    
    // Paints
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Particles and effects
    private val particles = mutableListOf<Particle>()
    private val orbs = mutableListOf<Orb>()
    private val sparks = mutableListOf<Spark>()
    
    // Animators
    private var rotationAnimator: ValueAnimator? = null
    private var pulseAnimator: ValueAnimator? = null
    private var glowAnimator: ValueAnimator? = null
    private var sparkleAnimator: ValueAnimator? = null

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null) // Hardware acceleration for smooth rendering
        initializeParticles()
        startAnimations()
    }

    private fun initializeParticles() {
        // Create floating particles around the reactor
        for (i in 0..40) {
            particles.add(Particle())
        }
        
        // Create orbiting orbs
        for (i in 0..8) {
            orbs.add(Orb(i * 40f))
        }
        
        // Create sparks
        for (i in 0..20) {
            sparks.add(Spark())
        }
    }

    private fun startAnimations() {
        // Smooth continuous rotation
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 6000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                rotation = it.animatedValue as Float
                invalidate()
            }
            start()
        }

        // Pulse wave effect
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                pulseRadius = it.animatedValue as Float
                invalidate()
            }
            start()
        }

        // Glow intensity pulsing
        glowAnimator = ValueAnimator.ofFloat(0.6f, 1f, 0.6f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                glowIntensity = it.animatedValue as Float
                invalidate()
            }
            start()
        }

        // Sparkle effect
        sparkleAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                sparklePhase = it.animatedValue as Float
                updateParticles()
                updateOrbs()
                updateSparks()
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val cx = width / 2f
        val cy = height / 2f
        val maxRadius = minOf(width, height) / 2f - 40

        // Draw background glow effects
        drawBackgroundGlow(canvas, cx, cy, maxRadius)
        
        // Draw pulse wave
        drawPulseWave(canvas, cx, cy, maxRadius)
        
        // Draw particles
        drawParticles(canvas, cx, cy)
        
        // Draw outer rotating rings
        drawOuterRings(canvas, cx, cy, maxRadius)
        
        // Draw orbiting orbs
        drawOrbs(canvas, cx, cy, maxRadius)
        
        // Draw arc segments with glow
        drawArcSegments(canvas, cx, cy, maxRadius)
        
        // Draw sparks
        drawSparks(canvas, cx, cy, maxRadius)
        
        // Draw the glowing core
        drawCore(canvas, cx, cy, maxRadius)
        
        // Draw energy lines
        drawEnergyLines(canvas, cx, cy, maxRadius)
        
        // Draw Mercedes logo
        drawMercedesLogo(canvas, cx, cy)
    }

    private fun drawBackgroundGlow(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        // Multiple layers of glow for depth
        val glowLayers = listOf(
            Pair(radius * 1.2f, 0.05f),
            Pair(radius * 1.0f, 0.1f),
            Pair(radius * 0.8f, 0.15f)
        )
        
        glowLayers.forEach { (r, alpha) ->
            val shader = RadialGradient(
                cx, cy, r,
                intArrayOf(
                    Color.argb((255 * alpha * glowIntensity).toInt(), 255, 200, 0),
                    Color.argb((150 * alpha * glowIntensity).toInt(), 255, 120, 0),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            glowPaint.shader = shader
            canvas.drawCircle(cx, cy, r, glowPaint)
        }
    }

    private fun drawPulseWave(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        if (pulseRadius > 0.1f) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            paint.color = Color.parseColor("#FFD700")
            paint.alpha = ((1 - pulseRadius) * 200).toInt()
            canvas.drawCircle(cx, cy, radius * 0.5f + (radius * 0.4f * pulseRadius), paint)
        }
    }

    private fun drawParticles(canvas: Canvas, cx: Float, cy: Float) {
        paint.style = Paint.Style.FILL
        particles.forEach { particle ->
            val x = cx + particle.x
            val y = cy + particle.y
            paint.color = particle.color
            paint.alpha = particle.alpha
            canvas.drawCircle(x, y, particle.size, paint)
        }
    }

    private fun drawOuterRings(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.save()
        canvas.rotate(rotation, cx, cy)
        
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        
        // Outer ring with gradient
        val outerRadius = radius * 0.95f
        paint.strokeWidth = 5f
        val shader = LinearGradient(
            cx - outerRadius, cy, cx + outerRadius, cy,
            intArrayOf(
                Color.argb(100, 255, 215, 0),
                Color.argb(200, 255, 165, 0),
                Color.argb(100, 255, 215, 0)
            ),
            null,
            Shader.TileMode.MIRROR
        )
        paint.shader = shader
        canvas.drawCircle(cx, cy, outerRadius, paint)
        paint.shader = null
        
        // Middle ring
        paint.strokeWidth = 4f
        paint.color = Color.parseColor("#FFA500")
        paint.alpha = (180 * glowIntensity).toInt()
        canvas.drawCircle(cx, cy, radius * 0.75f, paint)
        
        // Inner ring
        paint.strokeWidth = 3f
        paint.color = Color.parseColor("#FF8C00")
        paint.alpha = 255
        canvas.drawCircle(cx, cy, radius * 0.55f, paint)
        
        canvas.restore()
    }

    private fun drawArcSegments(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.save()
        canvas.rotate(-rotation * 1.5f, cx, cy)
        
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 6f
        
        val arcRadius = radius * 0.65f
        val sweepAngle = 30f
        
        for (i in 0..5) {
            val startAngle = i * 60f
            
            // Glow effect
            paint.color = Color.parseColor("#FFD700")
            paint.alpha = (100 * glowIntensity).toInt()
            paint.strokeWidth = 12f
            canvas.drawArc(
                cx - arcRadius, cy - arcRadius,
                cx + arcRadius, cy + arcRadius,
                startAngle, sweepAngle, false, paint
            )
            
            // Main arc
            paint.color = Color.parseColor("#FFA500")
            paint.alpha = 255
            paint.strokeWidth = 6f
            canvas.drawArc(
                cx - arcRadius, cy - arcRadius,
                cx + arcRadius, cy + arcRadius,
                startAngle, sweepAngle, false, paint
            )
        }
        
        canvas.restore()
    }

    private fun drawOrbs(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.save()
        
        orbs.forEach { orb ->
            val angle = Math.toRadians((orb.angle + rotation * 0.5).toDouble())
            val orbRadius = radius * 0.85f
            val x = cx + (orbRadius * cos(angle)).toFloat()
            val y = cy + (orbRadius * sin(angle)).toFloat()
            
            // Orb glow
            val glowShader = RadialGradient(
                x, y, orb.size * 3,
                intArrayOf(
                    Color.argb((150 * glowIntensity).toInt(), 255, 200, 0),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            glowPaint.shader = glowShader
            canvas.drawCircle(x, y, orb.size * 3, glowPaint)
            
            // Orb core
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#FFFF00")
            paint.alpha = 255
            canvas.drawCircle(x, y, orb.size, paint)
        }
        
        canvas.restore()
    }

    private fun drawSparks(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        
        sparks.forEach { spark ->
            if (spark.life > 0) {
                val x = cx + spark.x
                val y = cy + spark.y
                paint.strokeWidth = spark.size
                paint.color = Color.parseColor("#FFD700")
                paint.alpha = (spark.life * 255).toInt()
                canvas.drawLine(x, y, x + spark.vx * 3, y + spark.vy * 3, paint)
            }
        }
    }

    private fun drawCore(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        paint.style = Paint.Style.FILL
        val coreRadius = radius * 0.45f
        
        // Outer core glow with radial gradient
        val coreGlowShader = RadialGradient(
            cx, cy, coreRadius * 1.3f,
            intArrayOf(
                Color.argb((200 * glowIntensity).toInt(), 255, 230, 100),
                Color.argb((150 * glowIntensity).toInt(), 255, 200, 50),
                Color.argb((50 * glowIntensity).toInt(), 255, 150, 0),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.3f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = coreGlowShader
        canvas.drawCircle(cx, cy, coreRadius * 1.3f, glowPaint)
        
        // Main core
        val coreShader = RadialGradient(
            cx, cy, coreRadius,
            intArrayOf(
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#FFFF99"),
                Color.parseColor("#FFE135")
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = coreShader
        canvas.drawCircle(cx, cy, coreRadius, paint)
        paint.shader = null
        
        // Bright center spot
        paint.color = Color.WHITE
        paint.alpha = (255 * glowIntensity).toInt()
        canvas.drawCircle(cx, cy, coreRadius * 0.5f, paint)
        
        // Ultra-bright center
        paint.alpha = (200 * glowIntensity).toInt()
        canvas.drawCircle(cx, cy, coreRadius * 0.2f, paint)
    }

    private fun drawEnergyLines(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.save()
        canvas.rotate(rotation * 2, cx, cy)
        
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 2f
        
        for (i in 0..2) {
            val angle = Math.toRadians((i * 120).toDouble())
            val startRadius = radius * 0.15f
            val endRadius = radius * 0.45f
            
            val startX = cx + (startRadius * cos(angle)).toFloat()
            val startY = cy + (startRadius * sin(angle)).toFloat()
            val endX = cx + (endRadius * cos(angle)).toFloat()
            val endY = cy + (endRadius * sin(angle)).toFloat()
            
            // Glow
            paint.color = Color.parseColor("#FFD700")
            paint.alpha = (100 * glowIntensity).toInt()
            paint.strokeWidth = 6f
            canvas.drawLine(startX, startY, endX, endY, paint)
            
            // Main line
            paint.color = Color.parseColor("#FFFF00")
            paint.alpha = 255
            paint.strokeWidth = 2f
            canvas.drawLine(startX, startY, endX, endY, paint)
        }
        
        canvas.restore()
    }

    private fun drawMercedesLogo(canvas: Canvas, cx: Float, cy: Float) {
        val logoRadius = 35f
        
        // Logo background with subtle glow
        val logoShader = RadialGradient(
            cx, cy, logoRadius * 1.5f,
            intArrayOf(
                Color.argb(230, 255, 255, 255),
                Color.argb(180, 200, 220, 255)
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.FILL
        paint.shader = logoShader
        canvas.drawCircle(cx, cy, logoRadius, paint)
        paint.shader = null
        
        // Circle outline
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#001a4d")
        paint.strokeWidth = 3f
        paint.alpha = 255
        canvas.drawCircle(cx, cy, logoRadius - 3, paint)
        
        // Mercedes three-pointed star
        paint.strokeWidth = 3.5f
        paint.strokeCap = Paint.Cap.ROUND
        
        // Top point
        canvas.drawLine(cx, cy, cx, cy - logoRadius + 8, paint)
        
        // Bottom left point
        val angle1 = Math.toRadians(210.0)
        canvas.drawLine(
            cx, cy,
            cx + ((logoRadius - 8) * cos(angle1)).toFloat(),
            cy + ((logoRadius - 8) * sin(angle1)).toFloat(),
            paint
        )
        
        // Bottom right point
        val angle2 = Math.toRadians(330.0)
        canvas.drawLine(
            cx, cy,
            cx + ((logoRadius - 8) * cos(angle2)).toFloat(),
            cy + ((logoRadius - 8) * sin(angle2)).toFloat(),
            paint
        )
        
        // Center circle
        paint.style = Paint.Style.FILL
        canvas.drawCircle(cx, cy, 4f, paint)
    }

    private fun updateParticles() {
        particles.forEach { it.update() }
    }

    private fun updateOrbs() {
        orbs.forEach { it.update() }
    }

    private fun updateSparks() {
        sparks.forEach { it.update() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        rotationAnimator?.cancel()
        pulseAnimator?.cancel()
        glowAnimator?.cancel()
        sparkleAnimator?.cancel()
    }

    // Particle class for floating particles
    private inner class Particle {
        var x = Random.nextFloat() * 400 - 200
        var y = Random.nextFloat() * 400 - 200
        var vx = Random.nextFloat() * 2 - 1
        var vy = Random.nextFloat() * 2 - 1
        val size = Random.nextFloat() * 3 + 1
        var alpha = Random.nextInt(100, 255)
        val color = when (Random.nextInt(3)) {
            0 -> Color.parseColor("#FFD700")
            1 -> Color.parseColor("#FFA500")
            else -> Color.parseColor("#FFFF00")
        }
        
        fun update() {
            x += vx
            y += vy
            
            val distance = kotlin.math.sqrt(x * x + y * y)
            if (distance > 200) {
                val angle = Random.nextFloat() * 2 * Math.PI
                val radius = Random.nextFloat() * 50 + 150
                x = (radius * cos(angle)).toFloat()
                y = (radius * sin(angle)).toFloat()
            }
        }
    }

    // Orb class for orbiting energy orbs
    private inner class Orb(var angle: Float) {
        val size = Random.nextFloat() * 5 + 3
        
        fun update() {
            angle += 0.5f
            if (angle >= 360f) angle -= 360f
        }
    }

    // Spark class for electric sparks
    private inner class Spark {
        var x = Random.nextFloat() * 300 - 150
        var y = Random.nextFloat() * 300 - 150
        var vx = Random.nextFloat() * 4 - 2
        var vy = Random.nextFloat() * 4 - 2
        var life = Random.nextFloat()
        val size = Random.nextFloat() * 2 + 1
        
        fun update() {
            x += vx
            y += vy
            life -= 0.02f
            
            if (life <= 0) {
                val angle = Random.nextFloat() * 2 * Math.PI
                val radius = Random.nextFloat() * 100 + 100
                x = (radius * cos(angle)).toFloat()
                y = (radius * sin(angle)).toFloat()
                vx = Random.nextFloat() * 4 - 2
                vy = Random.nextFloat() * 4 - 2
                life = Random.nextFloat()
            }
        }
    }
}
