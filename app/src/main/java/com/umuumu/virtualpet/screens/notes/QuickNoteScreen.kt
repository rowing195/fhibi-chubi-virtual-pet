package com.umuumu.virtualpet.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.umuumu.virtualpet.R
import com.umuumu.virtualpet.ui.PetArtwork

@Composable
fun QuickNoteScreen(text: String, state: SaveState, onEdit: (String) -> Unit, onSave: () -> Unit, onCancel: () -> Unit) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    Surface(shape = MaterialTheme.shapes.extraLarge) {
        Column(Modifier.widthIn(max = 520.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.cream_notes), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                PetArtwork(Modifier.size(48.dp, 52.dp))
            }
            Text(stringResource(R.string.cream_note_intro), style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(value = text, onValueChange = onEdit, enabled = !state.busy, label = { Text(stringResource(R.string.note_hint)) }, minLines = 2, maxLines = 5, modifier = Modifier.fillMaxWidth().focusRequester(focus), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { onSave() }), isError = state.failed)
            if (state.failed) Text(stringResource(R.string.cream_save_failed), color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(shape = MaterialTheme.shapes.medium, onClick = onCancel, enabled = !state.busy, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.note_cancel)) }
                Button(shape = MaterialTheme.shapes.medium, onClick = onSave, enabled = text.isNotBlank() && !state.busy, modifier = Modifier.weight(1f)) { Text(stringResource(if (state.busy) R.string.cream_saving else R.string.note_save)) }
            }
        }
    }
}
