package com.umuumu.virtualpet

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/** Buttons of the pet's tap menu on a 3×3 grid around the pet; each bubble's tail points at the pet. */
enum class PetMenuItem(val column: Int, val row: Int, val bubble: Int, val label: Int?) {
    TOP_LEFT(-1, -1, R.drawable.pet_menu_top_left, R.string.menu_pet_actions),
    TOP(0, -1, R.drawable.pet_menu_top, R.string.menu_settings),
    TOP_RIGHT(1, -1, R.drawable.pet_menu_top_right, R.string.menu_quick_note),
    LEFT(-1, 0, R.drawable.pet_menu_left, null),
    RIGHT(1, 0, R.drawable.pet_menu_right, null),
    BOTTOM_LEFT(-1, 1, R.drawable.pet_menu_bottom_left, null),
    BOTTOM(0, 1, R.drawable.pet_menu_bottom, R.string.menu_close),
    BOTTOM_RIGHT(1, 1, R.drawable.pet_menu_bottom_right, null),
    ;

    /** Items without a label are reserved for features that don't exist yet. */
    val enabled: Boolean get() = label != null
}

class PetAction(val label: Int, val animation: PetAnimation)

/**
 * Full-screen menu drawn around the pet, whose on-screen bounds are [petBounds]. Tapping an enabled bubble reports it
 * through [onSelect]; tapping outside every bubble reports null so the menu can close.
 */
class PetMenuView(
    context: Context,
    private val petBounds: Rect,
    private val onSelect: (PetMenuItem?) -> Unit,
) : View(context) {
    private val bubbles = PetMenuItem.entries.associateWith { BitmapFactory.decodeResource(resources, it.bubble) }
    private val bubbleBounds = PetMenuItem.entries.associateWith { Rect() }
    private val bitmapPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = TEXT_COLOR
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SP, resources.displayMetrics)
    }
    private val comingSoon = context.getString(R.string.menu_coming_soon)
    private val location = IntArray(2)
    private var actions: Map<PetMenuItem, PetAction>? = null

    val showingActions: Boolean get() = actions != null

    fun showActions(slots: Map<PetMenuItem, PetAction>) {
        actions = slots
        invalidate()
    }

    fun showMain() {
        actions = null
        invalidate()
    }

    fun actionAt(item: PetMenuItem): PetAnimation? = actions?.get(item)?.animation

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // petBounds are screen coordinates; the window may not start at the screen's origin.
        getLocationOnScreen(location)
        val centerX = petBounds.centerX() - location[0]
        val centerY = petBounds.centerY() - location[1]

        val gap = dp(GAP_DP)
        val sample = bubbles.getValue(PetMenuItem.TOP)
        val bubbleWidth = minOf(dp(BUBBLE_WIDTH_DP), (width - 2 * dp(MARGIN_DP) - 2 * gap) / 3)
        val bubbleHeight = bubbleWidth * sample.height / sample.width
        for ((item, rect) in bubbleBounds) {
            val x = centerX + item.column * (bubbleWidth + gap)
            val y = centerY + item.row * (petBounds.height() / 2 + gap + bubbleHeight / 2)
            rect.set(x - bubbleWidth / 2, y - bubbleHeight / 2, x + bubbleWidth / 2, y + bubbleHeight / 2)
        }
    }

    override fun onDraw(canvas: Canvas) {
        for ((item, rect) in bubbleBounds) {
            canvas.drawBitmap(bubbles.getValue(item), null, rect, bitmapPaint)
            // Reserved bubbles stay opaque (a see-through bubble looks muddy on dark wallpapers); only the label fades.
            val labelId = if (actions == null) item.label else if (item == PetMenuItem.BOTTOM) R.string.menu_back else actions?.get(item)?.label
            textPaint.alpha = if (labelId != null) 255 else DISABLED_TEXT_ALPHA
            val label = labelId?.let(context::getString) ?: comingSoon
            val baseline = rect.exactCenterY() - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(label, rect.exactCenterX(), baseline, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            performClick()
            val item = bubbleBounds.entries.find { it.value.contains(event.x.toInt(), event.y.toInt()) }?.key
            val enabled = if (actions == null) item?.enabled == true else item == PetMenuItem.BOTTOM || actions?.containsKey(item) == true
            when {
                item == null -> onSelect(null)
                enabled -> onSelect(item)
                // Taps on reserved bubbles do nothing, so they don't close the menu by accident.
            }
        }
        return true
    }

    override fun performClick(): Boolean = super.performClick()

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val BUBBLE_WIDTH_DP = 118
        const val GAP_DP = 2
        const val MARGIN_DP = 8
        const val TEXT_SP = 16f
        const val DISABLED_TEXT_ALPHA = 90
        const val TEXT_COLOR = 0xFF3E2A23.toInt()
    }
}
