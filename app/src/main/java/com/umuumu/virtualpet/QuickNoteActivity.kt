package com.umuumu.virtualpet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umuumu.virtualpet.screens.notes.QuickNoteScreen
import com.umuumu.virtualpet.screens.notes.QuickNoteViewModel
import com.umuumu.virtualpet.ui.PetTheme

class QuickNoteActivity : ComponentActivity() {
    private val model by viewModels<QuickNoteViewModel>()
    private var saved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clearing = intent.getBooleanExtra(EXTRA_CLEAR_DONE, false)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or if (clearing) WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN else WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        setFinishOnTouchOutside(false)
        setContent {
            val text by model.text.collectAsStateWithLifecycle()
            val state by model.state.collectAsStateWithLifecycle()
            LaunchedEffect(state.saved) {
                if (state.saved) {
                    saved = true
                    if (!clearing) Toast.makeText(this@QuickNoteActivity, R.string.note_saved, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            PetTheme {
                if (clearing) {
                    AlertDialog(
                        onDismissRequest = { if (!state.busy) finish() },
                        title = { Text(stringResource(R.string.cream_clear_title)) },
                        text = { Text(stringResource(if (state.failed) R.string.cream_load_failed else R.string.cream_clear_hint)) },
                        confirmButton = { TextButton(onClick = { model.save(clearDone = true) }, enabled = !state.busy) { Text(stringResource(R.string.cream_clear)) } },
                        dismissButton = { TextButton(onClick = { finish() }, enabled = !state.busy) { Text(stringResource(R.string.cream_cancel)) } },
                    )
                } else Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    QuickNoteScreen(text, state, model::edit, { model.save() }, { finish() })
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        PetOverlayService.sendNoteState(this, PetOverlayService.NoteState.OPENED)
    }

    override fun onStop() {
        val state = when {
            saved -> PetOverlayService.NoteState.SAVED
            isFinishing -> PetOverlayService.NoteState.CANCELLED
            else -> PetOverlayService.NoteState.CLOSED
        }
        PetOverlayService.sendNoteState(this, state)
        super.onStop()
    }

    companion object { const val EXTRA_CLEAR_DONE = "clear_done" }
}
