package com.umuumu.virtualpet

import android.content.Intent
import android.content.ComponentName
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ListView
import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CreamInterfaceTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun settingsIntentReusesMainActivity() {
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            lateinit var original: MainActivity
            scenario.onActivity {
                original = it
                it.startActivity(Intent(it, MainActivity::class.java)
                    .putExtra(MainActivity.EXTRA_SCREEN, "settings")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }
            compose.waitUntil(5_000) {
                compose.onAllNodes(hasTestTag("nav-settings") and isSelected()).fetchSemanticsNodes().isNotEmpty()
            }
            scenario.onActivity { assertSame(original, it) }
            screenshot("settings")
        }
    }

    @Test fun quickNoteRetainsDraftAcrossRecreationAndSavesOnce() {
        val text = "介面測試-${System.nanoTime()}"
        val repository = TodoRepository.get(context)
        try {
            ActivityScenario.launch<QuickNoteActivity>(Intent(context, QuickNoteActivity::class.java)).use { scenario ->
                compose.onNodeWithText("記下").assertIsNotEnabled()
                compose.onNode(hasSetTextAction()).performClick().performTextInput(text)
                scenario.recreate()
                compose.onNode(hasSetTextAction()).assertTextContains(text)
                screenshot("quick-note")
                compose.onNodeWithText("記下", useUnmergedTree = true).performScrollTo().performClick()
                compose.waitUntil(5_000) { repository.all().count { it.text == text } == 1 }
                assertEquals(1, repository.all().count { it.text == text })
            }
        } finally { repository.writableDatabase.delete("todos", "text = ?", arrayOf(text)) }
    }

    @Test fun widgetToggleUpdatesOpenListAndNavigationSurvivesRecreation() {
        val text = "小工具測試-${System.nanoTime()}"
        val repository = TodoRepository.get(context)
        repository.add(text)
        val id = repository.all().single { it.text == text }.id
        try {
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
                screenshot("home")
                compose.onNodeWithTag("nav-notes").performClick()
                compose.onNodeWithText(text).assertExists()
                scenario.recreate()
                compose.onNodeWithTag("nav-notes").assertIsSelected()
                context.sendBroadcast(Intent(context, TodoActionReceiver::class.java).setAction(TodoActionReceiver.ACTION_TOGGLE).putExtra(TodoActionReceiver.EXTRA_TODO_ID, id))
                compose.waitUntil(5_000) { repository.all().single { it.id == id }.done }
                compose.waitUntil(5_000) {
                    compose.onAllNodes(hasText(text) and isToggleable() and isOn()).fetchSemanticsNodes().isNotEmpty()
                }
                compose.onNode(hasText(text) and isToggleable()).assertIsOn()
                screenshot("notes")
                compose.onNodeWithText("清除已完成").performScrollTo().performClick()
                compose.onNodeWithText("清除", substring = false).performClick()
                compose.waitUntil(5_000) { repository.all().none { it.id == id } }
                compose.onNodeWithTag("nav-settings").performClick()
                screenshot("settings")
            }
        } finally { repository.writableDatabase.delete("todos", "id = ?", arrayOf(id.toString())) }
    }

    private fun screenshot(name: String) {
        if (name != "quick-note") compose.mainClock.advanceTimeBy(500)
        InstrumentationRegistry.getInstrumentation().uiAutomation.waitForIdle(500, 5_000)
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        File(context.getExternalFilesDir(null), "qa-$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    @Test fun remoteWidgetRendersAndTogglesItsRow() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val repository = TodoRepository.get(context)
        val text = "桌面驗證-${System.nanoTime()}"
        repository.add(text)
        val todo = repository.all().single { it.text == text }
        val host = AppWidgetHost(context, 20260923)
        var widgetId = 0
        instrumentation.uiAutomation.adoptShellPermissionIdentity("android.permission.BIND_APPWIDGET")
        try {
            widgetId = host.allocateAppWidgetId()
            val manager = AppWidgetManager.getInstance(context)
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 280)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 280)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 280)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 280)
            }
            assertTrue(manager.bindAppWidgetIdIfAllowed(widgetId, ComponentName(context, VirtualPetAppWidgetProvider::class.java), options))
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
                lateinit var view: AppWidgetHostView
                scenario.onActivity { activity ->
                    host.startListening()
                    view = host.createView(activity, widgetId, manager.getAppWidgetInfo(widgetId))
                    val frame = FrameLayout(activity)
                    frame.setBackgroundColor(activity.getColor(R.color.pet_page))
                    val density = activity.resources.displayMetrics.density
                    frame.addView(view, FrameLayout.LayoutParams((280 * density).toInt(), (280 * density).toInt()).apply { leftMargin = (20 * density).toInt(); topMargin = (60 * density).toInt() })
                    activity.setContentView(frame)
                }
                compose.waitUntil(10_000) {
                    var loaded = false
                    instrumentation.runOnMainSync { loaded = (view.findViewById<ListView>(R.id.todo_list)?.childCount ?: 0) > 0 }
                    loaded
                }
                instrumentation.uiAutomation.waitForIdle(500, 5_000)
                val bitmap = instrumentation.uiAutomation.takeScreenshot()
                File(context.getExternalFilesDir(null), "qa-widget.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                bitmap.recycle()
                instrumentation.runOnMainSync {
                    val list = view.findViewById<ListView>(R.id.todo_list)
                    list.performItemClick(list.getChildAt(0), 0, list.adapter.getItemId(0))
                }
                compose.waitUntil(5_000) { repository.all().single { it.id == todo.id }.done }
            }
        } finally {
            host.stopListening()
            if (widgetId != 0) host.deleteAppWidgetId(widgetId)
            instrumentation.uiAutomation.dropShellPermissionIdentity()
            repository.writableDatabase.delete("todos", "id = ?", arrayOf(todo.id.toString()))
        }
    }
}
