package com.umuumu.virtualpet.screens.settings

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.umuumu.virtualpet.MenuEntry
import com.umuumu.virtualpet.MenuFeature
import com.umuumu.virtualpet.PetMenuItem
import com.umuumu.virtualpet.PetMenuPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LaunchableApp(val label: String, val component: ComponentName)
data class PetMenuEditorState(
    val slots: Map<PetMenuItem, MenuEntry>,
    val apps: List<LaunchableApp> = emptyList(),
)

class PetMenuEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val store = PetMenuPreferences(application)
    private val mutableState = MutableStateFlow(PetMenuEditorState(store.read()))
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val manager = getApplication<Application>().packageManager
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            @Suppress("DEPRECATION")
            val apps = manager.queryIntentActivities(intent, 0)
                .filter { it.activityInfo.packageName != getApplication<Application>().packageName }
                .distinctBy { it.activityInfo.packageName }
                .map { LaunchableApp(it.loadLabel(manager).toString(), ComponentName(it.activityInfo.packageName, it.activityInfo.name)) }
                .sortedBy { it.label.lowercase() }
            mutableState.update { it.copy(apps = apps) }
        }
    }

    fun swap(first: PetMenuItem, second: PetMenuItem) {
        if (first == second) return
        val slots = mutableState.value.slots.toMutableMap()
        val entry = slots.getValue(first)
        slots[first] = slots.getValue(second)
        slots[second] = entry
        save(slots)
    }

    fun assignFeature(slot: PetMenuItem, feature: MenuFeature) {
        val currentSlot = mutableState.value.slots.entries.first { it.value == MenuEntry.Feature(feature) }.key
        swap(slot, currentSlot)
    }

    fun assignApp(slot: PetMenuItem, component: ComponentName) {
        val slots = mutableState.value.slots.toMutableMap()
        val displaced = slots.getValue(slot)
        if (displaced is MenuEntry.Feature) {
            val emptySlot = slots.entries.firstOrNull { it.value == MenuEntry.Empty }?.key ?: return
            slots[emptySlot] = displaced
        }
        slots[slot] = MenuEntry.App(component)
        save(slots)
    }

    fun removeApp(slot: PetMenuItem) {
        if (mutableState.value.slots[slot] is MenuEntry.App) save(mutableState.value.slots + (slot to MenuEntry.Empty))
    }

    fun reset() {
        store.reset()
        mutableState.update { it.copy(slots = store.read()) }
    }

    private fun save(slots: Map<PetMenuItem, MenuEntry>) {
        store.save(slots)
        mutableState.update { it.copy(slots = slots) }
    }
}
