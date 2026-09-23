package com.umuumu.virtualpet

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Todo(val id: Long, val text: String, val done: Boolean)

/** Todo storage shared by the quick-note dialog and the home-screen widget. */
class TodoRepository private constructor(context: Context) :
    SQLiteOpenHelper(context, "todos.db", null, 1) {
    private val revision = MutableStateFlow(0L)
    val changes = revision.asStateFlow()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE todos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "text TEXT NOT NULL, " +
                "done INTEGER NOT NULL DEFAULT 0, " +
                "created_at INTEGER NOT NULL)",
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    /** Unfinished items first, newest first within each group. */
    fun all(): List<Todo> =
        readableDatabase.query(
            "todos",
            arrayOf("id", "text", "done"),
            null,
            null,
            null,
            null,
            "done ASC, created_at DESC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(Todo(cursor.getLong(0), cursor.getString(1), cursor.getInt(2) == 1))
                }
            }
        }

    fun add(text: String) {
        writableDatabase.insertOrThrow(
            "todos",
            null,
            ContentValues().apply {
                put("text", text)
                put("created_at", System.currentTimeMillis())
            },
        )
        revision.update { it + 1 }
    }

    fun toggle(id: Long) {
        writableDatabase.execSQL("UPDATE todos SET done = 1 - done WHERE id = ?", arrayOf(id))
        revision.update { it + 1 }
    }

    fun clearDone() {
        writableDatabase.delete("todos", "done = 1", null)
        revision.update { it + 1 }
    }

    companion object {
        @Volatile
        private var instance: TodoRepository? = null

        fun get(context: Context): TodoRepository =
            instance ?: synchronized(this) {
                instance ?: TodoRepository(context.applicationContext).also { instance = it }
            }
    }
}
