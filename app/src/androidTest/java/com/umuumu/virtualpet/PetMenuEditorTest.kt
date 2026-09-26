package com.umuumu.virtualpet

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.umuumu.virtualpet.screens.settings.PetMenuEditorViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PetMenuEditorTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test fun tappingAnotherSlotOnlyChangesSelection() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = PetMenuPreferences(context)
        val original = store.read()
        try {
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_SCREEN, "settings")).use {
                compose.onNodeWithText(context.getString(R.string.menu_editor_title)).performClick()
                compose.onNodeWithTag("menu-slot-TOP_LEFT").performClick()
                compose.onNodeWithTag("menu-slot-LEFT").performClick()
                compose.onNodeWithText(context.getString(R.string.menu_editor_selected, context.getString(PetMenuItem.LEFT.positionLabel))).assertExists()
                assertEquals(original, store.read())
            }
        } finally {
            store.save(original)
        }
    }

    @Test fun choosingInternalFeatureSwapsItsCurrentSlot() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = PetMenuPreferences(context)
        val original = store.read()
        try {
            store.reset()
            val defaults = store.read()
            val feature = MenuFeature.QUICK_NOTE
            val source = defaults.entries.first { it.value == MenuEntry.Feature(feature) }.key
            val target = defaults.entries.first { it.value == MenuEntry.Empty }.key
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_SCREEN, "settings")).use {
                compose.onNodeWithText(context.getString(R.string.menu_editor_title)).performClick()
                compose.onNodeWithTag("menu-slot-${target.name}").performClick()
                compose.onNodeWithTag("menu-feature-${feature.name}").performScrollTo().performClick()
                val saved = store.read()
                assertEquals(MenuEntry.Feature(feature), saved.getValue(target))
                assertEquals(MenuEntry.Empty, saved.getValue(source))
                assertEquals(defaults.values.toSet(), saved.values.toSet())
            }
        } finally {
            store.save(original)
        }
    }

    @Test fun assigningAppToFeatureMovesItIntoAnEmptySlot() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = PetMenuPreferences(context)
        val original = store.read()
        try {
            store.reset()
            val defaults = store.read()
            val target = defaults.entries.first { it.value is MenuEntry.Feature }.key
            val empty = defaults.entries.first { it.value == MenuEntry.Empty }.key
            val component = ComponentName("example.app", "example.app.Main")
            PetMenuEditorViewModel(context.applicationContext as Application).assignApp(target, component)
            val saved = store.read()
            assertEquals(MenuEntry.App(component), saved.getValue(target))
            assertEquals(defaults.getValue(target), saved.getValue(empty))
            assertEquals(
                defaults.values.filterIsInstance<MenuEntry.Feature>().toSet(),
                saved.values.filterIsInstance<MenuEntry.Feature>().toSet(),
            )
        } finally {
            store.save(original)
        }
    }
}
