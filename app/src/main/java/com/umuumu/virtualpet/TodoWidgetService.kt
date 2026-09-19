package com.umuumu.virtualpet

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.widget.RemoteViews
import android.widget.RemoteViewsService

/** Supplies the rows of the widget's todo list. */
class TodoWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = TodoViewsFactory(applicationContext)
}

private class TodoViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var todos = emptyList<Todo>()

    override fun onCreate() = Unit

    override fun onDataSetChanged() {
        todos = TodoRepository.get(context).all()
    }

    override fun onDestroy() = Unit

    override fun getCount(): Int = todos.size

    override fun getViewAt(position: Int): RemoteViews {
        val todo = todos[position]
        return RemoteViews(context.packageName, R.layout.widget_todo_item).apply {
            if (todo.done) {
                val text = SpannableString(todo.text).apply {
                    setSpan(StrikethroughSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                setTextViewText(R.id.todo_text, text)
                setTextColor(R.id.todo_text, context.getColor(R.color.widget_text_done))
                setImageViewResource(R.id.todo_check, R.drawable.ic_check_box)
            } else {
                setTextViewText(R.id.todo_text, todo.text)
                setTextColor(R.id.todo_text, context.getColor(R.color.widget_text))
                setImageViewResource(R.id.todo_check, R.drawable.ic_check_box_blank)
            }
            setOnClickFillInIntent(
                R.id.todo_item,
                Intent().putExtra(TodoActionReceiver.EXTRA_TODO_ID, todo.id),
            )
        }
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = todos[position].id

    override fun hasStableIds(): Boolean = true
}
