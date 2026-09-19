package com.umuumu.virtualpet

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

/** One cell of `pet_spritesheet.png`. */
class Cell(val row: Int, val column: Int)

private fun cells(row: Int, columns: List<Int>): List<Cell> = columns.map { Cell(row, it) }

private fun cells(row: Int, count: Int): List<Cell> = cells(row, (0 until count).toList())

/**
 * Idle row columns 0, 1, 3, 4 are open-eyed breathing poses; 2 and 5 are blinks. Played straight through,
 * the row blinks about three times a second, so breathe with open eyes and blink once every few seconds.
 */
private val IDLE_FRAMES = run {
    val breathe = listOf(0, 1, 3, 4)
    cells(0, breathe + breathe + breathe + listOf(0, 1, 2, 3, 4) + breathe + breathe + breathe + listOf(0, 1, 3, 4, 5))
}

/**
 * Note-taking alternates the busy row (7) with the waiting row (6), like jotting a few words and then pausing.
 * Busy column 3 is a squint, so it appears once per loop instead of every pass.
 */
private val WRITING_FRAMES = run {
    val scribble = listOf(0, 1, 2, 4, 5)
    cells(7, scribble + scribble + listOf(0, 1, 2, 3, 4, 5)) + cells(6, listOf(0, 1, 2, 3, 4, 5, 5, 5))
}

/** Frame counts per row come from the atlas's pet_request.json. */
enum class PetAnimation(val frames: List<Cell>, val frameMs: Long = 120L) {
    IDLE(IDLE_FRAMES, frameMs = 200L),
    RUN_RIGHT(cells(1, 8)),
    RUN_LEFT(cells(2, 8)),
    WAVE(cells(3, 4)),
    JUMP(cells(4, 5)),
    FAILED(cells(5, 8), frameMs = 150L),
    WRITING(WRITING_FRAMES, frameMs = 180L),
    REVIEW(cells(8, 6), frameMs = 160L),
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
        if (frame < animation.frames.size - 1) {
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
        postDelayed(tick, animation.frameMs)
    }

    private fun restartTicking() {
        removeCallbacks(tick)
        postDelayed(tick, animation.frameMs)
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
        val cell = animation.frames[frame]
        val left = cell.column * CELL_WIDTH
        val top = cell.row * CELL_HEIGHT
        src.set(left, top, left + CELL_WIDTH, top + CELL_HEIGHT)
        dst.set(0, 0, width, height)
        canvas.drawBitmap(sheet, src, dst, paint)
    }

    companion object {
        const val CELL_WIDTH = 192
        const val CELL_HEIGHT = 208
    }
}
