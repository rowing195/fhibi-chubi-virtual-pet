package com.umuumu.virtualpet.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.umuumu.virtualpet.R
import com.umuumu.virtualpet.ui.PetArtwork

@Composable
fun HomeScreen(running: Boolean, permitted: Boolean, onPet: () -> Unit, onPermission: () -> Unit, onNote: () -> Unit, onWidget: () -> Unit, onReply: (() -> Unit)?) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.cream_brand), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.cream_greeting), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
        Card(shape = MaterialTheme.shapes.extraLarge, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(if (running) R.string.cream_active else R.string.cream_resting), style = MaterialTheme.typography.bodyMedium)
                PetArtwork(Modifier.size(144.dp, 156.dp))
                Text(stringResource(R.string.cream_hero_title), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.cream_hero_hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (!permitted) {
            Text(stringResource(R.string.cream_permission_intro), style = MaterialTheme.typography.bodyMedium)
            Button(shape = MaterialTheme.shapes.medium, onClick = onPermission, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.grant_overlay)) }
        } else {
            FilledTonalButton(shape = MaterialTheme.shapes.medium, onClick = onPet, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) { Text(stringResource(if (running) R.string.pet_hide else R.string.pet_show)) }
        }
        Text(stringResource(R.string.cream_quick_actions), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (onReply == null || LocalDensity.current.fontScale >= 1.3f) {
            Shortcut(R.string.cream_notes, R.string.cream_note_hint, onNote, Modifier.fillMaxWidth())
            if (onReply != null) Shortcut(R.string.cream_reply, R.string.cream_reply_hint, onReply, Modifier.fillMaxWidth())
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Shortcut(R.string.cream_notes, R.string.cream_note_hint, onNote, Modifier.weight(1f))
                Shortcut(R.string.cream_reply, R.string.cream_reply_hint, onReply, Modifier.weight(1f))
            }
        }
        Shortcut(R.string.cream_widget_title, R.string.cream_widget_hint, onWidget, Modifier.fillMaxWidth())
    }
}

@Composable
private fun Shortcut(title: Int, hint: Int, onClick: () -> Unit, modifier: Modifier) {
    OutlinedCard(onClick = onClick, modifier = modifier, shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
