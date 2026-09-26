package com.umuumu.virtualpet

import android.content.ComponentName
import android.content.Context

enum class MenuFeature(val label: Int) {
    PET_ACTIONS(R.string.menu_pet_actions),
    SETTINGS(R.string.menu_settings),
    QUICK_NOTE(R.string.menu_quick_note),
    CLOSE(R.string.menu_close),
}

sealed interface MenuEntry {
    data class Feature(val feature: MenuFeature) : MenuEntry
    data class App(val component: ComponentName) : MenuEntry
    data object Empty : MenuEntry
}

fun MenuEntry.label(context: Context): String? = when (this) {
    is MenuEntry.Feature -> context.getString(feature.label)
    is MenuEntry.App -> try {
        @Suppress("DEPRECATION")
        context.packageManager.getActivityInfo(component, 0).loadLabel(context.packageManager).toString()
    } catch (_: Exception) {
        context.getString(R.string.menu_app_missing)
    }
    MenuEntry.Empty -> null
}

class PetMenuPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("pet_menu", Context.MODE_PRIVATE)

    fun read(): Map<PetMenuItem, MenuEntry> = PetMenuItem.entries.associateWith { slot ->
        val saved = prefs.getString(slot.name, null) ?: return@associateWith DEFAULTS.getValue(slot)
        when {
            saved == "empty" -> MenuEntry.Empty
            saved.startsWith("feature:") -> MenuFeature.entries.firstOrNull { it.name == saved.removePrefix("feature:") }
                ?.let { MenuEntry.Feature(it) } ?: DEFAULTS.getValue(slot)
            saved.startsWith("app:") -> ComponentName.unflattenFromString(saved.removePrefix("app:"))
                ?.let { MenuEntry.App(it) } ?: DEFAULTS.getValue(slot)
            else -> DEFAULTS.getValue(slot)
        }
    }

    fun save(slots: Map<PetMenuItem, MenuEntry>) {
        prefs.edit().apply {
            for (slot in PetMenuItem.entries) {
                putString(slot.name, when (val entry = slots.getValue(slot)) {
                    is MenuEntry.Feature -> "feature:${entry.feature.name}"
                    is MenuEntry.App -> "app:${entry.component.flattenToString()}"
                    MenuEntry.Empty -> "empty"
                })
            }
        }.apply()
    }

    fun reset() = prefs.edit().clear().apply()

    companion object {
        val DEFAULTS: Map<PetMenuItem, MenuEntry> = mapOf(
            PetMenuItem.TOP_LEFT to MenuEntry.Feature(MenuFeature.PET_ACTIONS),
            PetMenuItem.TOP to MenuEntry.Feature(MenuFeature.SETTINGS),
            PetMenuItem.TOP_RIGHT to MenuEntry.Feature(MenuFeature.QUICK_NOTE),
            PetMenuItem.LEFT to MenuEntry.Empty,
            PetMenuItem.RIGHT to MenuEntry.Empty,
            PetMenuItem.BOTTOM_LEFT to MenuEntry.Empty,
            PetMenuItem.BOTTOM to MenuEntry.Feature(MenuFeature.CLOSE),
            PetMenuItem.BOTTOM_RIGHT to MenuEntry.Empty,
        )
    }
}
