package com.umuumu.virtualpet

import androidx.core.content.ContextCompat
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.hypot

/** Shows the pet in a draggable overlay window that snaps to the nearest screen edge. */
class PetOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var petView: PetSpriteView
    private lateinit var params: WindowManager.LayoutParams
    private var snapAnimator: ValueAnimator? = null
    private var menuView: PetMenuView? = null
    private var mainEntries: Map<PetMenuItem, MenuEntry> = PetMenuPreferences.DEFAULTS
    private var menuOpen = false
    private var positionBeforeMenu = Rect()
    private var noteOpen = false

    private val noteStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(EXTRA_NOTE_STATE)?.let(NoteState::valueOf)) {
                NoteState.OPENED -> {
                    noteOpen = true
                    rest()
                }
                NoteState.SAVED -> {
                    noteOpen = false
                    petView.play(PetAnimation.REVIEW) { rest() }
                }
                NoteState.CANCELLED -> {
                    noteOpen = false
                    petView.play(PetAnimation.FAILED) { rest() }
                }
                NoteState.CLOSED -> {
                    noteOpen = false
                    rest()
                }
                null -> Unit
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startInForeground()

        windowManager = getSystemService(WindowManager::class.java)
        val height = dp(PET_HEIGHT_DP)
        params = WindowManager.LayoutParams(
            height * PetSpriteView.CELL_WIDTH / PetSpriteView.CELL_HEIGHT,
            height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = resources.displayMetrics.heightPixels / 3
        }

        petView = PetSpriteView(this)
        petView.setOnClickListener { openMenu() }
        petView.setOnTouchListener(DragListener())
        windowManager.addView(petView, params)
        petView.play(PetAnimation.WAVE) { rest() }
        runningState.value = true

        val filter = IntentFilter(ACTION_NOTE_STATE)
        ContextCompat.registerReceiver(this, noteStateReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        runningState.value = false
        unregisterReceiver(noteStateReceiver)
        snapAnimator?.cancel()
        menuView?.let(windowManager::removeView)
        windowManager.removeView(petView)
        super.onDestroy()
    }

    private fun startInForeground() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.pet_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
        val openApp = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val stopPet = PendingIntent.getService(
            this,
            0,
            Intent(this, PetOverlayService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(getString(R.string.pet_notification_title))
            .setContentIntent(openApp)
            .addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(this, R.drawable.ic_launcher_monochrome),
                    getString(R.string.pet_hide),
                    stopPet,
                ).build(),
            )
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    /** Writes along while a note is open, otherwise idles. */
    private fun rest() {
        petView.play(if (noteOpen) PetAnimation.WRITING else PetAnimation.IDLE)
    }

    private fun moveTo(x: Int, y: Int) {
        val metrics = resources.displayMetrics
        params.x = x.coerceIn(0, metrics.widthPixels - params.width)
        params.y = y.coerceIn(0, metrics.heightPixels - params.height)
        windowManager.updateViewLayout(petView, params)
    }

    private fun snapToEdge() {
        val screenWidth = resources.displayMetrics.widthPixels
        val targetX = if (params.x + params.width / 2 < screenWidth / 2) 0 else screenWidth - params.width
        petView.play(PetAnimation.JUMP) { rest() }
        glideTo(targetX, params.y) {}
    }

    private fun glideTo(x: Int, y: Int, durationMs: Long = SNAP_MS, onArrived: () -> Unit) {
        snapAnimator?.cancel()
        val fromX = params.x
        val fromY = params.y
        snapAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = durationMs
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val t = it.animatedValue as Float
                moveTo(fromX + ((x - fromX) * t).toInt(), fromY + ((y - fromY) * t).toInt())
            }
            addListener(object : AnimatorListenerAdapter() {
                private var cancelled = false

                override fun onAnimationCancel(animation: Animator) {
                    cancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!cancelled) onArrived()
                }
            })
            start()
        }
    }

    private fun openMenu() {
        if (menuOpen) return
        menuOpen = true
        positionBeforeMenu = Rect(params.x, params.y, params.x + params.width, params.y + params.height)
        val metrics = resources.displayMetrics
        val centerX = (metrics.widthPixels - params.width) / 2
        petView.play(if (centerX > params.x) PetAnimation.RUN_RIGHT else PetAnimation.RUN_LEFT)
        glideTo(centerX, (metrics.heightPixels - params.height) / 2, MENU_RUN_MS) {
            val bounds = Rect(params.x, params.y, params.x + params.width, params.y + params.height)
            mainEntries = PetMenuPreferences(this).read()
            val menu = PetMenuView(this, bounds, mainEntries, ::onMenuSelected)
            windowManager.addView(menu, WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT,
            ))
            menuView = menu
            petView.play(PetAnimation.WAVE) { rest() }
        }
    }

    private fun closeMenu() {
        menuView?.let(windowManager::removeView)
        menuView = null
        petView.play(PetAnimation.JUMP) { rest() }
        glideTo(positionBeforeMenu.left, positionBeforeMenu.top, MENU_GLIDE_MS) {
            menuOpen = false
        }
    }

    private fun onMenuSelected(item: PetMenuItem?) {
        val menu = menuView
        if (menu?.showingActions == true) {
            when (item) {
                null -> closeMenu()
                PetMenuItem.BOTTOM -> {
                    menu.showMain()
                    rest()
                }
                else -> menu.actionAt(item)?.let { petView.play(it) { rest() } }
            }
            return
        }
        when (val entry = item?.let(mainEntries::get)) {
            is MenuEntry.Feature -> when (entry.feature) {
                MenuFeature.PET_ACTIONS -> {
                    menu?.showActions(ACTION_SLOTS)
                    petView.play(PetAnimation.IDLE)
                }
                MenuFeature.SETTINGS -> {
                    closeMenu()
                    startActivity(Intent(this, MainActivity::class.java)
                        .putExtra(MainActivity.EXTRA_SCREEN, "settings")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                }
                MenuFeature.QUICK_NOTE -> {
                    closeMenu()
                    startActivity(Intent(this, QuickNoteActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
                MenuFeature.CLOSE -> stopSelf()
            }
            is MenuEntry.App -> {
                closeMenu()
                try {
                    startActivity(Intent.makeMainActivity(entry.component).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (_: ActivityNotFoundException) {
                    android.widget.Toast.makeText(this, R.string.menu_app_missing, android.widget.Toast.LENGTH_SHORT).show()
                } catch (_: SecurityException) {
                    android.widget.Toast.makeText(this, R.string.menu_app_missing, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            else -> closeMenu()
        }
    }

    private inner class DragListener : View.OnTouchListener {
        private val touchSlop = ViewConfiguration.get(this@PetOverlayService).scaledTouchSlop
        private val directionSlop = dp(DIRECTION_SLOP_DP)
        private var downRawX = 0f
        private var downRawY = 0f
        private var directionRawX = 0f
        private var startX = 0
        private var startY = 0
        private var dragging = false

        @SuppressLint("ClickableViewAccessibility") // taps are forwarded through performClick()
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (menuOpen) return true
                    snapAnimator?.cancel()
                    downRawX = event.rawX
                    downRawY = event.rawY
                    directionRawX = event.rawX
                    startX = params.x
                    startY = params.y
                    dragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downRawX
                    val dy = event.rawY - downRawY
                    if (!dragging && hypot(dx, dy) > touchSlop) dragging = true
                    if (dragging) {
                        moveTo(startX + dx.toInt(), startY + dy.toInt())
                        val step = event.rawX - directionRawX
                        if (abs(step) > directionSlop) {
                            petView.play(if (step > 0) PetAnimation.RUN_RIGHT else PetAnimation.RUN_LEFT)
                            directionRawX = event.rawX
                        }
                    }
                }

                MotionEvent.ACTION_UP -> if (dragging) snapToEdge() else view.performClick()
                MotionEvent.ACTION_CANCEL -> if (dragging) snapToEdge()
            }
            return true
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    /** CLOSED means the dialog was merely hidden (e.g. Home pressed); CANCELLED means it was dismissed unsaved. */
    enum class NoteState { OPENED, SAVED, CANCELLED, CLOSED }

    companion object {
        private val ACTION_SLOTS = mapOf(
            PetMenuItem.TOP_LEFT to PetAction(R.string.menu_action_idle, PetAnimation.IDLE),
            PetMenuItem.TOP to PetAction(R.string.menu_action_run_both, PetAnimation.RUN_BOTH),
            PetMenuItem.TOP_RIGHT to PetAction(R.string.menu_action_wave, PetAnimation.WAVE),
            PetMenuItem.LEFT to PetAction(R.string.menu_action_jump, PetAnimation.JUMP),
            PetMenuItem.RIGHT to PetAction(R.string.menu_action_failed, PetAnimation.FAILED),
            PetMenuItem.BOTTOM_LEFT to PetAction(R.string.menu_action_review, PetAnimation.REVIEW),
            PetMenuItem.BOTTOM_RIGHT to PetAction(R.string.menu_action_writing, PetAnimation.WRITING),
        )
        private val runningState = MutableStateFlow(false)
        val running = runningState.asStateFlow()
        const val ACTION_STOP = "com.umuumu.virtualpet.action.STOP_PET"
        private const val ACTION_NOTE_STATE = "com.umuumu.virtualpet.action.NOTE_STATE"
        private const val EXTRA_NOTE_STATE = "note_state"
        private const val CHANNEL_ID = "pet_overlay"
        private const val NOTIFICATION_ID = 1
        private const val PET_HEIGHT_DP = 80
        private const val DIRECTION_SLOP_DP = 4
        private const val SNAP_MS = 250L
        private const val MENU_RUN_MS = 350L
        private const val MENU_GLIDE_MS = 500L

        /** Lets the pet react to the quick-note dialog; ignored when the pet isn't showing. */
        fun sendNoteState(context: Context, state: NoteState) {
            context.sendBroadcast(
                Intent(ACTION_NOTE_STATE)
                    .setPackage(context.packageName)
                    .putExtra(EXTRA_NOTE_STATE, state.name),
            )
        }
    }
}
