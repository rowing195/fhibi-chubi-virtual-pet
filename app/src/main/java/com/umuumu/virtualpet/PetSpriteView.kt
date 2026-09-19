package com.umuumu.virtualpet

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

/** Rows of `pet_spritesheet.png`; frame counts come from the atlas's pet_request.json. */
enum class PetAnimation(val row: Int, val frameCount: Int) {
    IDLE(0, 6),
    RUN_RIGHT(1, 8),
    RUN_LEFT(2, 8),
    WAVE(3, 4),
    JUMP(4, 5),
}

class PetSpriteView(context: Context) : View(context) {
    private val sheet = BitmapFactory.decodeResource(resources, R.drawable.pet_spritesheet)
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val src = Rect()
    private val dst = Rect()
    private val tick = Runnable { advance() }

    private var animation = PetAnimation.IDLE
    private var frame = 0
    private var onFinished: (() -> Unit)? = null

    /** Loops [animation] when [onFinished] is null; otherwise plays it once and invokes [onFinished]. */
    fun play(animation: PetAnimation, onFinished: (() -> Unit)? = null) {
        if (animation == this.animation && onFinished == null && this.onFinished == null) return
        this.animation = animation
        this.onFinished = onFinished
        frame = 0
        invalidate()
        restartTicking()
    }

    private fun advance() {
        if (frame < animation.frameCount - 1) {
            frame++
        } else {
            val finished = onFinished
            if (finished != null) {
                onFinished = null
                finished()
                return
            }
            frame = 0
        }
        invalidate()
        postDelayed(tick, FRAME_MS)
    }

    private fun restartTicking() {
        removeCallbacks(tick)
        postDelayed(tick, FRAME_MS)
    }

    override fun performClick(): Boolean = super.performClick()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        restartTicking()
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(tick)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        val left = frame * CELL_WIDTH
        val top = animation.row * CELL_HEIGHT
        src.set(left, top, left + CELL_WIDTH, top + CELL_HEIGHT)
        dst.set(0, 0, width, height)
        canvas.drawBitmap(sheet, src, dst, paint)
    }

    companion object {
        const val CELL_WIDTH = 192
        const val CELL_HEIGHT = 208
        private const val FRAME_MS = 120L
    }
}
