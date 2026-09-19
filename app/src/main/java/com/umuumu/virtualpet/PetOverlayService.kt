package com.umuumu.virtualpet

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs
import kotlin.math.hypot

/** Shows the pet in a draggable overlay window that snaps to the nearest screen edge. */
class PetOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var petView: PetSpriteView
    private lateinit var params: WindowManager.LayoutParams
    private var snapAnimator: ValueAnimator? = null
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
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = resources.displayMetrics.heightPixels / 3
        }

        petView = PetSpriteView(this)
        petView.setOnClickListener {
            startActivity(Intent(this, QuickNoteActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        petView.setOnTouchListener(DragListener())
        windowManager.addView(petView, params)
        petView.play(PetAnimation.WAVE) { rest() }

        val filter = IntentFilter(ACTION_NOTE_STATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(noteStateReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(noteStateReceiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(noteStateReceiver)
        snapAnimator?.cancel()
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
        snapAnimator = ValueAnimator.ofInt(params.x, targetX).apply {
            duration = SNAP_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener { moveTo(it.animatedValue as Int, params.y) }
            start()
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
        const val ACTION_STOP = "com.umuumu.virtualpet.action.STOP_PET"
        private const val ACTION_NOTE_STATE = "com.umuumu.virtualpet.action.NOTE_STATE"
        private const val EXTRA_NOTE_STATE = "note_state"
        private const val CHANNEL_ID = "pet_overlay"
        private const val NOTIFICATION_ID = 1
        private const val PET_HEIGHT_DP = 80
        private const val DIRECTION_SLOP_DP = 4
        private const val SNAP_MS = 250L

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
