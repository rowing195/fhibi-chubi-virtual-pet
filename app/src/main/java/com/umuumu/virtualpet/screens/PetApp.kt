package com.umuumu.virtualpet.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.umuumu.virtualpet.R
import com.umuumu.virtualpet.screens.home.HomeScreen
import com.umuumu.virtualpet.screens.notes.NotesScreen
import com.umuumu.virtualpet.screens.notes.NotesState
import com.umuumu.virtualpet.screens.settings.SettingsScreen
import com.umuumu.virtualpet.screens.settings.PetMenuEditorScreen
import com.umuumu.virtualpet.screens.settings.PetMenuEditorViewModel
import com.umuumu.virtualpet.ui.PetArtwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetApp(destination: String, navigationRequest: Int, notes: NotesState, running: Boolean, permitted: Boolean, version: String, onPet: () -> Unit, onPermission: () -> Unit, onNote: () -> Unit, onToggle: (Long) -> Unit, onClear: () -> Unit, onRetry: () -> Unit, onAi: (() -> Unit)? = null, onReply: (() -> Unit)? = null) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"
    var handledRequest by rememberSaveable { mutableIntStateOf(-1) }
    fun navigate(target: String) {
        nav.navigate(target) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    LaunchedEffect(destination, navigationRequest, entry != null) {
        if (entry != null && handledRequest != navigationRequest && destination in listOf("home", "notes", "settings")) {
            navigate(destination)
            handledRequest = navigationRequest
        }
    }
    val pages = listOf("home" to R.string.cream_home, "notes" to R.string.cream_notes, "settings" to R.string.cream_settings)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (route != "home" && route != "menu-editor") TopAppBar(
                title = { Text(stringResource(pages.find { it.first == route }?.second ?: R.string.cream_widget)) },
                navigationIcon = { if (route == "widget") IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Default.ArrowBack, stringResource(R.string.cream_back)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            if (route != "widget" && route != "menu-editor") NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                pages.forEach { (page, label) ->
                    NavigationBarItem(modifier = Modifier.testTag("nav-$page"), selected = route == page, onClick = { navigate(page) }, icon = { Icon(when (page) { "home" -> Icons.Default.Home; "notes" -> Icons.AutoMirrored.Default.List; else -> Icons.Default.Settings }, null) }, label = { Text(stringResource(label)) })
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding), contentAlignment = Alignment.TopCenter) {
            NavHost(nav, startDestination = "home", modifier = Modifier.widthIn(max = 600.dp).fillMaxSize()) {
                composable("home") { HomeScreen(running, permitted, onPet, onPermission, onNote, { nav.navigate("widget") }, onReply) }
                composable("notes") { NotesScreen(notes, onNote, onToggle, onClear, onRetry) }
                composable("settings") { SettingsScreen(running, permitted, version, onPet, onPermission, { nav.navigate("widget") }, { nav.navigate("menu-editor") }, onAi) }
                composable("widget") { WidgetGuide(onNote) }
                composable("menu-editor") {
                    val model: PetMenuEditorViewModel = viewModel()
                    val state by model.state.collectAsStateWithLifecycle()
                    PetMenuEditorScreen(state, model::assignFeature, model::assignApp, model::removeApp, model::reset) { nav.popBackStack() }
                }
            }
        }
    }
}

@Composable
private fun WidgetGuide(onNote: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Card(shape = MaterialTheme.shapes.extraLarge) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.cream_notes), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    PetArtwork(Modifier.size(48.dp, 52.dp))
                }
                Text(stringResource(R.string.cream_widget_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(shape = MaterialTheme.shapes.medium, onClick = onNote) { Text(stringResource(R.string.cream_notes)) }
            }
        }
        Text(stringResource(R.string.cream_widget_preview), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.cream_widget_guide))
    }
}
