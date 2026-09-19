package com.umuumu.virtualpet

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/** Dialog opened by tapping the pet or the widget's add button. */
class QuickNoteActivity : Activity() {
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_note)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        input = findViewById(R.id.note_input)
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                save()
                true
            } else {
                false
            }
        }
        findViewById<Button>(R.id.note_save).setOnClickListener { save() }
        findViewById<Button>(R.id.note_cancel).setOnClickListener { finish() }
    }

    private fun save() {
        val text = input.text.toString().trim()
        // Enter can fire the editor action on both key down and key up; save only once.
        if (text.isEmpty() || isFinishing) return
        TodoRepository.get(this).add(text)
        VirtualPetAppWidgetProvider.refreshAll(this)
        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}
