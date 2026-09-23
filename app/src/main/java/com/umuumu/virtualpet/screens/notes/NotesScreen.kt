package com.umuumu.virtualpet.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.umuumu.virtualpet.R
import com.umuumu.virtualpet.ui.PetArtwork

@Composable
fun NotesScreen(state: NotesState, onAdd: () -> Unit, onToggle: (Long) -> Unit, onClear: () -> Unit, onRetry: () -> Unit) {
    var confirming by rememberSaveable { mutableStateOf(false) }
    if (confirming) ClearDoneDialog(onDismiss = { confirming = false }, onConfirm = { confirming = false; onClear() })
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(stringResource(R.string.cream_notes_intro), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item { Text(stringResource(R.string.cream_pending, state.todos.count { !it.done }), style = MaterialTheme.typography.titleLarge) }
        item {
            Button(shape = MaterialTheme.shapes.medium, onClick = onAdd, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.cream_notes))
            }
        }
        if (state.loading) item { CircularProgressIndicator() }
        if (state.failed) item {
            Text(stringResource(R.string.cream_load_failed), color = MaterialTheme.colorScheme.error)
            TextButton(onClick = onRetry) { Text(stringResource(R.string.cream_retry)) }
        }
        if (!state.loading && !state.failed && state.todos.isEmpty()) item {
            Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PetArtwork(Modifier.size(120.dp, 130.dp))
                Text(stringResource(R.string.cream_empty_title), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.cream_empty_hint), style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(state.todos, key = { it.id }) { todo ->
            Surface(shape = MaterialTheme.shapes.medium) {
                Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).toggleable(todo.done, role = Role.Checkbox, onValueChange = { onToggle(todo.id) }).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Checkbox(todo.done, onCheckedChange = null)
                    Text(todo.text, modifier = Modifier.weight(1f), textDecoration = if (todo.done) TextDecoration.LineThrough else TextDecoration.None, color = if (todo.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item {
            TextButton(onClick = { confirming = true }, enabled = state.todos.any { it.done }) { Text(stringResource(R.string.widget_clear_done)) }
            Text(stringResource(R.string.cream_shared_notes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ClearDoneDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.cream_clear_title)) }, text = { Text(stringResource(R.string.cream_clear_hint)) }, confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.cream_clear)) } }, dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cream_cancel)) } })
}
