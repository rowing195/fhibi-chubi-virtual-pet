package com.umuumu.virtualpet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TodoActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_TOGGLE && intent.action != ACTION_CLEAR_DONE) return
        val pending = goAsync()
        Thread {
            try {
                val repository = TodoRepository.get(context)
                when (intent.action) {
                    ACTION_TOGGLE -> repository.toggle(intent.getLongExtra(EXTRA_TODO_ID, -1))
                    ACTION_CLEAR_DONE -> repository.clearDone()
                }
                VirtualPetAppWidgetProvider.refreshAll(context)
            } finally { pending.finish() }
        }.start()
    }

    companion object {
        const val ACTION_TOGGLE = "com.umuumu.virtualpet.action.TOGGLE_TODO"
        const val ACTION_CLEAR_DONE = "com.umuumu.virtualpet.action.CLEAR_DONE_TODOS"
        const val EXTRA_TODO_ID = "todo_id"
    }
}
