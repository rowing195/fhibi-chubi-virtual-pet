package com.umuumu.virtualpet.screens.notes

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.umuumu.virtualpet.TodoRepository
import com.umuumu.virtualpet.VirtualPetAppWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SaveState(val busy: Boolean = false, val saved: Boolean = false, val failed: Boolean = false)

class QuickNoteViewModel(application: Application, private val savedState: SavedStateHandle) : AndroidViewModel(application) {
    val text = savedState.getStateFlow("text", "")
    private val mutableState = MutableStateFlow(SaveState())
    val state = mutableState.asStateFlow()

    fun edit(value: String) { savedState["text"] = value }

    fun save(clearDone: Boolean = false) {
        if (state.value.busy || state.value.saved || (!clearDone && text.value.isBlank())) return
        val value = text.value.trim()
        mutableState.value = SaveState(busy = true)
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val repository = TodoRepository.get(getApplication())
                    if (clearDone) repository.clearDone() else repository.add(value)
                    VirtualPetAppWidgetProvider.refreshAll(getApplication())
                }
                mutableState.value = SaveState(saved = true)
            } catch (_: SQLiteException) {
                mutableState.value = SaveState(failed = true)
            }
        }
    }
}
