package com.tallyho.scorekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = GameStore(applicationContext)
        setContent {
            TallyhoTheme {
                when (store.phase) {
                    Phase.SETUP -> SetupScreen(store)
                    Phase.GAME -> GameScreen(store)
                    Phase.RESULTS -> ResultsScreen(store)
                }
            }
        }
    }
}

@Composable
fun TallyhoTheme(content: @Composable () -> Unit) {
    // Fixed teal scheme built from the icon's #00796B (no wallpaper-based dynamic color).
    val scheme = if (isSystemInDarkTheme()) TallyhoDark else TallyhoLight
    MaterialTheme(colorScheme = scheme, content = content)
}

private val TallyhoLight = lightColorScheme(
    primary = Color(0xFF00796B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF00201C),
    secondary = Color(0xFF4A635F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8E2),
    onSecondaryContainer = Color(0xFF05201C),
    tertiary = Color(0xFFB07A00),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF4FBF9),
    onBackground = Color(0xFF161D1C),
    surface = Color(0xFFF4FBF9),
    onSurface = Color(0xFF161D1C),
    surfaceVariant = Color(0xFFDAE5E1),
    onSurfaceVariant = Color(0xFF3F4947),
    outline = Color(0xFF6F7977),
    outlineVariant = Color(0xFFBEC9C6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF5F3),
    surfaceContainer = Color(0xFFE9EFED),
    surfaceContainerHigh = Color(0xFFE3EAE8),
    surfaceContainerHighest = Color(0xFFDDE4E2),
)

private val TallyhoDark = darkColorScheme(
    primary = Color(0xFF84D6CA),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF00504A),
    onPrimaryContainer = Color(0xFFA0F2E6),
    secondary = Color(0xFFB1CCC6),
    onSecondary = Color(0xFF1C3531),
    secondaryContainer = Color(0xFF334B47),
    onSecondaryContainer = Color(0xFFCCE8E2),
    tertiary = Color(0xFFFFC857),
    onTertiary = Color(0xFF402D00),
    background = Color(0xFF0E1513),
    onBackground = Color(0xFFDDE4E2),
    surface = Color(0xFF0E1513),
    onSurface = Color(0xFFDDE4E2),
    surfaceVariant = Color(0xFF3F4947),
    onSurfaceVariant = Color(0xFFBEC9C6),
    outline = Color(0xFF899390),
    outlineVariant = Color(0xFF3F4947),
    surfaceContainerLowest = Color(0xFF090F0E),
    surfaceContainerLow = Color(0xFF161D1C),
    surfaceContainer = Color(0xFF1A2120),
    surfaceContainerHigh = Color(0xFF252B2A),
    surfaceContainerHighest = Color(0xFF2F3635),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(store: GameStore) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Tallyho — Teams") }) },
        bottomBar = {
            Button(
                onClick = { store.startGame() },
                enabled = store.teams.size >= 2,
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 8.dp)
                    .height(56.dp),
            ) {
                Text(
                    if (store.teams.size >= 2) "START GAME" else "Add at least 2 teams",
                    fontSize = 18.sp,
                )
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(store.teams, key = { it.id }) { team ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = team.name,
                        onValueChange = { store.rename(team.id, it) },
                        placeholder = { Text(team.defaultName) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { store.removeTeam(team.id) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove ${team.displayName}")
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = { store.addTeam() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add team")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(store: GameStore) {
    var confirmEnd by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }

    // Keep the screen on during a game (no permission required).
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
    BackHandler { confirmEnd = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tallyho") },
                actions = {
                    TextButton(onClick = { confirmReset = true }) { Text("Reset") }
                    TextButton(onClick = { confirmEnd = true }) { Text("End") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(store.teams, key = { it.id }) { team ->
                TeamScoreCard(team, onChange = { store.changeScore(team.id, it) })
            }
        }
    }

    if (confirmReset) {
        ConfirmDialog(
            title = "Reset scores?",
            text = "Every team's score goes back to 0.",
            confirmLabel = "Reset",
            onConfirm = { store.resetScores(); confirmReset = false },
            onDismiss = { confirmReset = false },
        )
    }
    if (confirmEnd) {
        ConfirmDialog(
            title = "End game?",
            text = "You'll see the final standings, then you can set up the next game.",
            confirmLabel = "End game",
            onConfirm = { store.endGame(); confirmEnd = false },
            onDismiss = { confirmEnd = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(store: GameStore) {
    val standings = store.standings()
    val winners = standings.filter { it.first == 1 }.map { it.second }
    BackHandler { store.backToSetup() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Results") }) },
        bottomBar = {
            Button(
                onClick = { store.backToSetup() },
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 8.dp)
                    .height(56.dp),
            ) {
                Text("SET UP NEXT GAME", fontSize = 18.sp)
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    Text("🏆", fontSize = 64.sp)
                    Text(
                        if (winners.size > 1) "It's a tie!" else "Winner",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        winners.joinToString(" & ") { it.displayName },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            items(standings, key = { it.second.id }) { (place, team) ->
                val isWinner = place == 1
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (isWinner) {
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    } else {
                        CardDefaults.cardColors()
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(
                            ordinal(place),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(48.dp),
                        )
                        Text(
                            team.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            formatHalves(team.halves),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

fun ordinal(n: Int): String {
    val suffix = if (n % 100 in 11..13) "th" else when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
}

@Composable
fun TeamScoreCard(team: Team, onChange: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    team.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    formatHalves(team.halves),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ScoreButton("−1") { onChange(-2) }
                Spacer(Modifier.width(8.dp))
                ScoreButton("−½") { onChange(-1) }
                Spacer(Modifier.weight(1f))
                ScoreButton("+½", primary = true) { onChange(1) }
                Spacer(Modifier.width(8.dp))
                ScoreButton("+1", primary = true) { onChange(2) }
            }
        }
    }
}

@Composable
fun ScoreButton(label: String, primary: Boolean = false, onClick: () -> Unit) {
    val mod = Modifier
        .width(68.dp)
        .height(52.dp)
    val pad = PaddingValues(0.dp)
    if (primary) {
        Button(onClick = onClick, modifier = mod, contentPadding = pad) {
            Text(label, fontSize = 20.sp)
        }
    } else {
        FilledTonalButton(onClick = onClick, modifier = mod, contentPadding = pad) {
            Text(label, fontSize = 20.sp)
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
