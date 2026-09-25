package com.umuumu.virtualpet

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews

/** Home-screen todo list; taps are handled by [TodoActionReceiver]. */
class VirtualPetAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val pending = goAsync()
        Thread {
            try {
                appWidgetIds.forEach { appWidgetId ->
                    appWidgetManager.updateAppWidget(appWidgetId, buildViews(context))
                }
            } finally { pending.finish() }
        }.start()
    }

    @Suppress("DEPRECATION") // RemoteCollectionItems needs API 31; minSdk is 28.
    private fun buildViews(context: Context): RemoteViews {
        val toggleTemplate = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, TodoActionReceiver::class.java).setAction(TodoActionReceiver.ACTION_TOGGLE),
            PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0,
        )
        val clearDone = PendingIntent.getActivity(
            context,
            1,
            Intent(context, QuickNoteActivity::class.java).putExtra(QuickNoteActivity.EXTRA_CLEAR_DONE, true),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val addNote = PendingIntent.getActivity(
            context,
            0,
            Intent(context, QuickNoteActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE,
        )

        return RemoteViews(context.packageName, R.layout.widget_todo).apply {
            val todos = TodoRepository.get(context).all()
            setTextViewText(R.id.widget_count, context.getString(R.string.cream_widget_total, todos.size))
            setViewVisibility(R.id.todo_clear_done, if (todos.any { it.done }) View.VISIBLE else View.GONE)
            setRemoteAdapter(R.id.todo_list, Intent(context, TodoWidgetService::class.java))
            setEmptyView(R.id.todo_list, R.id.todo_empty)
            setPendingIntentTemplate(R.id.todo_list, toggleTemplate)
            setOnClickPendingIntent(R.id.todo_add, addNote)
            setOnClickPendingIntent(R.id.todo_clear_done, clearDone)
        }
    }

    companion object {
        /** Reloads the list in every placed widget after the todos change. */
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, VirtualPetAppWidgetProvider::class.java))
            val provider = VirtualPetAppWidgetProvider()
            ids.forEach { manager.updateAppWidget(it, provider.buildViews(context)) }
            manager.notifyAppWidgetViewDataChanged(ids, R.id.todo_list)
        }
    }
}
