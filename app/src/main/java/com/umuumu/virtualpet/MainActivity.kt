package com.umuumu.virtualpet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var grantButton: Button
    private lateinit var showButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.status)
        grantButton = findViewById(R.id.grant_overlay)
        showButton = findViewById(R.id.show_pet)

        grantButton.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
            )
        }
        showButton.setOnClickListener {
            startForegroundService(Intent(this, PetOverlayService::class.java))
        }
        findViewById<Button>(R.id.hide_pet).setOnClickListener {
            stopService(Intent(this, PetOverlayService::class.java))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }

    override fun onResume() {
        super.onResume()
        val canOverlay = Settings.canDrawOverlays(this)
        status.setText(if (canOverlay) R.string.overlay_granted else R.string.overlay_needed)
        grantButton.visibility = if (canOverlay) View.GONE else View.VISIBLE
        showButton.isEnabled = canOverlay
    }
}
