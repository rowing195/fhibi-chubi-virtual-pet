package com.umuumu.virtualpet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umuumu.virtualpet.screens.PetApp
import com.umuumu.virtualpet.screens.notes.NotesViewModel
import com.umuumu.virtualpet.ui.PetTheme

class MainActivity : ComponentActivity() {
    private val notes by viewModels<NotesViewModel>()
    private var permitted by mutableStateOf(false)
    private var destination by mutableStateOf("home")
    private var navigationRequest by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        destination = intent.getStringExtra(EXTRA_SCREEN) ?: "home"
        navigationRequest = savedInstanceState?.getInt("navigationRequest") ?: 0
        val version = packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
        setContent {
            val state by notes.state.collectAsStateWithLifecycle()
            val running by PetOverlayService.running.collectAsStateWithLifecycle()
            PetTheme {
                PetApp(
                    destination = destination, navigationRequest = navigationRequest,
                    notes = state, running = running, permitted = permitted, version = version,
                    onPet = {
                        if (running) stopService(Intent(this, PetOverlayService::class.java))
                        else if (Settings.canDrawOverlays(this)) startForegroundService(Intent(this, PetOverlayService::class.java))
                    },
                    onPermission = { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) },
                    onNote = { startActivity(Intent(this, QuickNoteActivity::class.java)) },
                    onToggle = notes::toggle, onClear = notes::clearDone, onRetry = notes::retry,
                )
            }
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        destination = intent.getStringExtra(EXTRA_SCREEN) ?: "home"
        navigationRequest++
    }

    override fun onResume() {
        super.onResume()
        permitted = Settings.canDrawOverlays(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("navigationRequest", navigationRequest)
        super.onSaveInstanceState(outState)
    }

    companion object { const val EXTRA_SCREEN = "screen" }
}
