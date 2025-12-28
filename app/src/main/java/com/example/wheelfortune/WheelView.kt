package com.example.wheelfortune

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.random.Random

class WheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val places = listOf(
        "Суши бар",
        "Пиццерия",
        "Бургерная",
        "Шаурма",
        "Кофейня",
        "Столовая",
        "Ресторан",
        "Кафе у дома"
    )

    private val colors = listOf(
        Color.parseColor("#FF5722"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#2196F3"),
        Color.parseColor("#FFC107"),
        Color.parseColor("#9C27B0"),
        Color.parseColor("#00BCD4"),
        Color.parseColor("#F44336"),
        Color.parseColor("#8BC34A")
    )

    private var currentAngle = 0f
    private var targetAngle = 0f
    private var isSpinning = false
    private var onSpinEndListener: ((String) -> Unit)? = null

    private val rectF = RectF()

    fun spin() {
        if (isSpinning) return
        isSpinning = true

        // Случайный угол: 5–10 полных оборотов + случайный сектор
        val spins = 5 + Random.nextInt(6) // 5–10 оборотов
        val extraDegrees = Random.nextFloat() * 360f
        targetAngle = currentAngle + spins * 360f + extraDegrees

        post(animationRunnable)
    }

    fun setOnSpinEndListener(listener: (String) -> Unit) {
        onSpinEndListener = listener
    }

    private val animationRunnable = object : Runnable {
        override fun run() {
            if (isSpinning) {
                val delta = (targetAngle - currentAngle).coerceAtLeast(0f)
                if (delta > 5f) {
                    currentAngle += delta * 0.15f // Замедление
                    invalidate()
                    postDelayed(this, 16)
                } else {
                    currentAngle = targetAngle
                    isSpinning = false
                    invalidate()

                    // Определяем выбранный сектор
                    val normalizedAngle = (360 - (currentAngle % 360)) % 360
                    val sectorAngle = 360f / places.size
                    val index = (normalizedAngle / sectorAngle).toInt()
                    onSpinEndListener?.invoke(places[index])
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = w.coerceAtMost(h)
        val padding = size * 0.1f
        rectF.set(padding, padding, size - padding, size - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val sweepAngle = 360f / places.size

        for (i in places.indices) {
            paint.color = colors[i % colors.size]
            canvas.save()
            canvas.rotate(i * sweepAngle + currentAngle, width / 2f, height / 2f)
            canvas.drawArc(rectF, 0f, sweepAngle, true, paint)
            canvas.restore()

            // Рисуем текст
            val textAngle = i * sweepAngle + sweepAngle / 2 + currentAngle
            val radius = width / 2f * 0.65f

            // Используем PI для преобразования градусов в радианы
            val radians = textAngle * PI.toFloat() / 180f
            val x = width / 2f + cos(radians) * radius
            val y = height / 2f + sin(radians) * radius

            canvas.save()
            canvas.rotate(textAngle + 90, x, y) // Поворот текста
            canvas.drawText(places[i], x, y + 12f, textPaint) // +12 для центрирования
            canvas.restore()
        }

        // Центральный круг (чтобы скрыть центр)
        paint.color = Color.WHITE
        canvas.drawCircle(width / 2f, height / 2f, width * 0.15f, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isSpinning = false
        removeCallbacks(animationRunnable)
        onSpinEndListener = null
    }
}