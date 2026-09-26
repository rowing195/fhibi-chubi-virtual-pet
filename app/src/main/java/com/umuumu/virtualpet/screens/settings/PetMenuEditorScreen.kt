package com.umuumu.virtualpet.screens.settings

import android.content.ComponentName
import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.umuumu.virtualpet.MenuEntry
import com.umuumu.virtualpet.MenuFeature
import com.umuumu.virtualpet.PetMenuItem
import com.umuumu.virtualpet.R
import com.umuumu.virtualpet.label
import com.umuumu.virtualpet.ui.PetArtwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetMenuEditorScreen(
    state: PetMenuEditorState,
    onAssignFeature: (PetMenuItem, MenuFeature) -> Unit,
    onAssignApp: (PetMenuItem, ComponentName) -> Unit,
    onRemoveApp: (PetMenuItem) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = PetMenuItem.entries.firstOrNull { it.name == selectedName }
    val selectedEntry = selected?.let { state.slots.getValue(it) }
    var showApps by rememberSaveable { mutableStateOf(false) }
    var showReset by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)), horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            title = { Text(stringResource(R.string.menu_editor_title)) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.cream_back)) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        )
        Column(Modifier.weight(1f).widthIn(max = 600.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(stringResource(R.string.menu_editor_hint), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in -1..1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        for (column in -1..1) {
                            val slot = PetMenuItem.entries.firstOrNull { it.row == row && it.column == column }
                            if (slot == null) {
                                Box(Modifier.weight(1f).heightIn(min = 94.dp), contentAlignment = Alignment.Center) {
                                    PetArtwork(Modifier.size(62.dp, 70.dp))
                                }
                            } else {
                                val entry = state.slots.getValue(slot)
                                OutlinedCard(
                                    onClick = { selectedName = slot.name },
                                    modifier = Modifier.weight(1f).heightIn(min = 94.dp).testTag("menu-slot-${slot.name}"),
                                    border = if (selected == slot) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
                                    colors = CardDefaults.outlinedCardColors(containerColor = if (selected == slot) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                                ) {
                                    Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(stringResource(slot.positionLabel), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            if (entry is MenuEntry.App) AppIcon(entry.component, Modifier.size(28.dp))
                                            Text(entry.label(context) ?: stringResource(R.string.menu_empty_slot), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (selected != null) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.menu_editor_selected, stringResource(selected.positionLabel)), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    if (selectedEntry is MenuEntry.App) TextButton(onClick = { onRemoveApp(selected); selectedName = null }) {
                        Text(stringResource(R.string.menu_remove_app))
                    }
                }
                Text(stringResource(R.string.menu_choose_feature), style = MaterialTheme.typography.titleSmall)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MenuFeature.entries.forEach { feature ->
                        val currentSlot = state.slots.entries.first { it.value == MenuEntry.Feature(feature) }.key
                        OutlinedButton(
                            onClick = { onAssignFeature(selected, feature); selectedName = null },
                            enabled = currentSlot != selected,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("menu-feature-${feature.name}"),
                        ) {
                            Text(stringResource(feature.label))
                            Spacer(Modifier.weight(1f))
                            Text(stringResource(currentSlot.positionLabel))
                        }
                    }
                    val canAddApp = selectedEntry !is MenuEntry.Feature || state.slots.values.contains(MenuEntry.Empty)
                    if (!canAddApp) Text(stringResource(R.string.menu_app_needs_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { showApps = true }, enabled = canAddApp, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Text(stringResource(R.string.menu_other_apps))
                    }
                }
            }
            TextButton(onClick = { showReset = true }) { Text(stringResource(R.string.menu_reset)) }
        }
    }

    if (showApps && selected != null) {
        var query by rememberSaveable { mutableStateOf("") }
        val apps = state.apps.filter { it.label.contains(query, ignoreCase = true) || it.component.packageName.contains(query, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { showApps = false },
            title = { Text(stringResource(R.string.menu_choose_app)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text(stringResource(R.string.menu_search_apps)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    if (apps.isEmpty()) Text(stringResource(R.string.menu_no_apps))
                    else LazyColumn(Modifier.heightIn(max = 360.dp)) {
                        items(apps, key = { it.component.flattenToString() }) { app ->
                            ListItem(
                                leadingContent = { AppIcon(app.component, Modifier.size(40.dp)) },
                                headlineContent = { Text(app.label) },
                                supportingContent = { Text(app.component.packageName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                modifier = Modifier.fillMaxWidth().clickable {
                                    onAssignApp(selected, app.component)
                                    selectedName = null
                                    showApps = false
                                },
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showApps = false }) { Text(stringResource(R.string.cream_back)) } },
        )
    }
    if (showReset) AlertDialog(
        onDismissRequest = { showReset = false },
        title = { Text(stringResource(R.string.menu_reset)) },
        text = { Text(stringResource(R.string.menu_reset_confirm)) },
        confirmButton = { TextButton(onClick = { onReset(); selectedName = null; showReset = false }) { Text(stringResource(R.string.menu_reset)) } },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text(stringResource(R.string.cream_cancel)) } },
    )
}

@Composable
private fun AppIcon(component: ComponentName, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val icon = remember(component) { runCatching { context.packageManager.getActivityIcon(component) }.getOrNull() }
    if (icon != null) AndroidView(
        factory = { ImageView(it).apply { scaleType = ImageView.ScaleType.FIT_CENTER } },
        update = { it.setImageDrawable(icon) },
        modifier = modifier,
    )
}
