package com.umuumu.virtualpet.screens.notes

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.umuumu.virtualpet.Todo
import com.umuumu.virtualpet.TodoRepository
import com.umuumu.virtualpet.VirtualPetAppWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NotesState(val todos: List<Todo> = emptyList(), val loading: Boolean = true, val failed: Boolean = false)

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TodoRepository.get(application)
    private val mutableState = MutableStateFlow(NotesState())
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.changes.collectLatest { reload() }
        }
    }

    fun retry() { viewModelScope.launch { reload() } }

    private suspend fun reload() {
        try {
            val todos = withContext(Dispatchers.IO) { repository.all() }
            mutableState.value = NotesState(todos, loading = false)
        } catch (_: SQLiteException) {
            mutableState.value = mutableState.value.copy(loading = false, failed = true)
        }
    }

    fun toggle(id: Long) = change { repository.toggle(id) }
    fun clearDone() = change { repository.clearDone() }

    private fun change(operation: () -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    operation()
                    VirtualPetAppWidgetProvider.refreshAll(getApplication())
                }
            } catch (_: SQLiteException) {
                mutableState.value = mutableState.value.copy(failed = true)
            }
        }
    }
}
