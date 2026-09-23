package com.umuumu.virtualpet.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.umuumu.virtualpet.R
import com.umuumu.virtualpet.ui.PetArtwork

@Composable
fun SettingsScreen(running: Boolean, permitted: Boolean, version: String, onPet: () -> Unit, onPermission: () -> Unit, onWidget: () -> Unit, onAi: (() -> Unit)?) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.cream_settings_intro), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PetArtwork(Modifier.size(72.dp, 78.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.cream_companion), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(if (running) R.string.cream_active else R.string.cream_resting), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(stringResource(R.string.cream_permissions), modifier = Modifier.semantics { heading() })
        Card {
            Row(Modifier.fillMaxWidth().toggleable(running, role = Role.Switch, onValueChange = { if (permitted) onPet() else onPermission() }).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.cream_show_pet), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.cream_show_pet_hint), style = MaterialTheme.typography.bodyMedium)
                }
                Switch(checked = running, onCheckedChange = null)
            }
        }
        SettingLink(R.string.cream_overlay, if (permitted) R.string.cream_allowed else R.string.cream_not_allowed, onPermission)
        Text(stringResource(R.string.cream_tools), modifier = Modifier.semantics { heading() })
        if (onAi != null) SettingLink(R.string.cream_ai_settings, R.string.cream_ai_hint, onAi)
        SettingLink(R.string.cream_widget, R.string.cream_widget_settings_hint, onWidget)
        Text(stringResource(R.string.cream_pet_tip), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.cream_version, version), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SettingLink(title: Int, hint: Int, onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
