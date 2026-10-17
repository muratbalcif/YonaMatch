package com.yonamatch.app

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.times
import com.yonamatch.app.data.AssociationLevelRepository
import com.yonamatch.app.data.MatchGame

/** Separate screen entry points; navigation selects one screen, not one shared page. */
@Composable fun MemoryGameScreen(content: @Composable () -> Unit) = content()
@Composable fun ShadowGameScreen(content: @Composable () -> Unit) = content()
@Composable fun ShapeGameScreen(content: @Composable () -> Unit) = content()
@Composable fun ColorGameScreen(content: @Composable () -> Unit) = content()
@Composable fun HalvesGameScreen(content: @Composable () -> Unit) = content()
/** Owns its level, selection, attempts and matching state independently of MainActivity. */
@Composable
fun AssociationGameScreen(level: Int, round: Int, onComplete: (Int, Boolean) -> Unit) {
    val context = LocalContext.current
    val levelData = remember(context, level) { AssociationLevelRepository(context).levels()[level - 1] }
    val pairs = levelData.pairs
    val targets = remember(level) { pairs.associate { it.source.id to it.target } }
    val game = remember(level, round) { MatchGame(pairs.map { it.source }, targets) }
    var selection by remember(level, round) { mutableStateOf<String?>(null) }
    var selectedRight by remember(level, round) { mutableStateOf<String?>(null) }
    var hintUsed by remember(level, round) { mutableStateOf(false) }
    var revision by remember(level, round) { mutableIntStateOf(0) }
    val cardHeight = ((LocalConfiguration.current.screenHeightDp.dp - 220.dp - 8.dp * (if (game.items.size <= 3) game.items.size - 1 else (game.items.size + 1) / 2 - 1)) / (if (game.items.size <= 3) game.items.size else (game.items.size + 1) / 2)).coerceAtLeast(76.dp)
    var incorrect by remember(level, round) { mutableStateOf<String?>(null) }
    val completed = game.matched.toSet()
    fun submit(source: String, target: String) {
        if (game.complete) return
        val correct = game.match(source, target)
        revision++
        incorrect = if (correct) null else target
        selection = null
        selectedRight = null
        if (game.complete) onComplete(game.attempts, hintUsed)
    }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("🔗", fontSize = 32.sp)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                selection = game.items.firstOrNull { it.id !in completed }?.id
                selectedRight = null
                if (selection != null) hintUsed = true
            }) { Text("💡", fontSize = 34.sp) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (game.items.size <= 3) 1 else 2),
                modifier = Modifier.weight(1f).height(cardHeight * (if (game.items.size <= 3) game.items.size else (game.items.size + 1) / 2) + 8.dp * (if (game.items.size <= 3) game.items.size - 1 else (game.items.size + 1) / 2 - 1)),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(game.items, key = { it.id }) { item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(cardHeight).clickable(enabled = item.id !in completed) { if (selectedRight != null) submit(item.id, selectedRight!!) else selection = item.id },
                        color = if (item.id in completed) Color(0xFFBDEDD7) else if (selection == item.id) Color(0xFFFFE4A0) else Color(0xFFE0F7EC),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (item.id in completed) "✓" else item.symbol, fontSize = 46.sp)
                        }
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (game.targets.size <= 3) 1 else 2),
                modifier = Modifier.weight(1f).height(cardHeight * (if (game.targets.size <= 3) game.targets.size else (game.targets.size + 1) / 2) + 8.dp * (if (game.targets.size <= 3) game.targets.size - 1 else (game.targets.size + 1) / 2 - 1)),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(game.targets, key = { it.id }) { item ->
                    val sourceId = targets.entries.first { it.value.id == item.id }.key
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(cardHeight).clickable(enabled = sourceId !in completed) {
                            if (selection != null) submit(selection!!, item.id) else selectedRight = item.id
                        },
                        color = if (sourceId in completed) Color(0xFFBDEDD7) else if (incorrect == item.id) Color(0xFFFFD6DC) else if (selectedRight == item.id) Color(0xFFFFE4A0) else Color(0xFFECE6FF),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (sourceId in completed) "✓" else item.symbol, fontSize = 46.sp)
                        }
                    }
                }
            }
        }
        if (incorrect != null) Text("↻", fontSize = 28.sp, color = Color(0xFFCC4455))
        if (revision > 0) Text("⭐ ${completed.size}/${game.items.size}", fontSize = 22.sp)
    }
}

/** Shared matching mechanics; each mode has its own screen entry and isolated round. */
@Composable
private fun PairMatchingScreen(
    mode: Int,
    level: Int,
    round: Int,
    onComplete: (Int, Boolean) -> Unit
) {
    val context = LocalContext.current
    val type = when (mode) { 2 -> "shape"; 3 -> "color"; else -> "halves" }
    val levelData = remember(context, type, level) {
        com.yonamatch.app.data.MatchLevelRepository(context).levels(type)[level - 1]
    }
    val game = remember(type, level, round) { MatchGame(levelData.items) }
    var selection by remember(type, level, round) { mutableStateOf<String?>(null) }
    var selectedRight by remember(type, level, round) { mutableStateOf<String?>(null) }
    var hintUsed by remember(type, level, round) { mutableStateOf(false) }
    var revision by remember(type, level, round) { mutableIntStateOf(0) }
    var wrongTarget by remember(type, level, round) { mutableStateOf<String?>(null) }
    val completed = game.matched.toSet()
    fun submit(source: String, target: String) {
        if (game.complete) return
        val correct = game.match(source, target)
        revision++
        wrongTarget = if (correct) null else target
        selection = null
        selectedRight = null
        if (game.complete) onComplete(game.attempts, hintUsed)
    }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(when (mode) { 2 -> "🔺"; 3 -> "🎨"; else -> "🧩" }, fontSize = 32.sp)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                selection = game.items.firstOrNull { it.id !in completed }?.id
                selectedRight = null
                if (selection != null) hintUsed = true
            }) { Text("💡", fontSize = 34.sp) }
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val rows = if (game.items.size <= 3) game.items.size else (game.items.size + 1) / 2
            val cardHeight = ((maxHeight - 8.dp * (rows - 1)) / rows).coerceAtLeast(64.dp)
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(false, true).forEach { isTarget ->
                val entries = if (isTarget) game.targets else game.items
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (entries.size <= 3) 1 else 2),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries, key = { it.id }) { item ->
                        val solved = item.id in completed
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(cardHeight).clickable(enabled = !solved) {
                                if (isTarget) {
                                    if (selection != null) submit(selection!!, item.id) else selectedRight = item.id
                                } else {
                                    if (selectedRight != null) submit(item.id, selectedRight!!) else selection = item.id
                                }
                            },
                            color = if (solved) Color(0xFFBDEDD7) else if (isTarget && wrongTarget == item.id) Color(0xFFFFD6DC)
                                else if (isTarget && selectedRight == item.id) Color(0xFFFFE4A0)
                                else if (!isTarget && selection == item.id) Color(0xFFFFE4A0)
                                else if (isTarget) Color(0xFFECE6FF) else Color(0xFFE0F7EC),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (solved) Text("✓", fontSize = 42.sp)
                                else if (mode == 4) {
                                    HalfVisual(item, left = !isTarget)
                                } else if (mode == 3) {
                                    val shade = when (item.id) {
                                        "red" -> Color.Red; "blue" -> Color.Blue; "green" -> Color.Green
                                        "yellow" -> Color.Yellow; "purple" -> Color.Magenta
                                        "orange" -> Color(0xFFFF9800); "pink" -> Color(0xFFFF69B4)
                                        else -> Color.Gray
                                    }
                                    Box(Modifier.size(54.dp).background(shade, androidx.compose.foundation.shape.CircleShape))
                                } else {
                                    Text(item.symbol, fontSize = 58.sp, color = if (isTarget && mode == 2) Color(0xFF9CA3AF) else Color(0xFF1F2937))
                                }
                            }
                        }
                    }
                }
            }
            }
        }
        if (wrongTarget != null) Text("↻", fontSize = 28.sp, color = Color(0xFFCC4455))
        if (revision > 0) Text("⭐ ${completed.size}/${game.items.size}", fontSize = 22.sp)
    }
}

@Composable fun ShapeGameScreen(level: Int, round: Int, onComplete: (Int, Boolean) -> Unit) =
    PairMatchingScreen(2, level, round, onComplete)

@Composable fun ColorGameScreen(level: Int, round: Int, onComplete: (Int, Boolean) -> Unit) =
    PairMatchingScreen(3, level, round, onComplete)

@Composable fun HalvesGameScreen(level: Int, round: Int, onComplete: (Int, Boolean) -> Unit) =
    PairMatchingScreen(4, level, round, onComplete)

@Composable
fun MemoryPlayScreen(level: Int, round: Int, onComplete: (Int, Boolean) -> Unit) {
    val context = LocalContext.current
    val levelData = remember(context, level) { com.yonamatch.app.data.LevelRepository(context).memoryLevels()[level - 1] }
    val cards = remember(level, round) {
        val icons = listOf("🦁", "🐘", "🐰", "🐼", "🐧", "🐱", "🐶", "🐻").take(levelData.pairs)
        (icons + icons).shuffled()
    }
    var revealed by remember(level, round) { mutableStateOf<List<Int>>(emptyList()) }
    var matched by remember(level, round) { mutableStateOf<Set<Int>>(emptySet()) }
    var attempts by remember(level, round) { mutableIntStateOf(0) }
    var hintUsed by remember(level, round) { mutableStateOf(false) }
    var hintPair by remember(level, round) { mutableStateOf<Set<Int>>(emptySet()) }
    var mismatch by remember(level, round) { mutableStateOf(false) }
    var mismatchToken by remember(level, round) { mutableIntStateOf(0) }
    LaunchedEffect(mismatchToken) {
        if (mismatch) {
            kotlinx.coroutines.delay(850)
            revealed = emptyList()
            mismatch = false
        }
    }
    val columns = if (cards.size <= 6) 3 else 4
    val rows = (cards.size + columns - 1) / columns
    val cardHeight = ((LocalConfiguration.current.screenHeightDp.dp - 210.dp - 8.dp * (rows - 1)) / rows).coerceAtLeast(76.dp)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("🦁", fontSize = 32.sp)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                val first = cards.indices.firstOrNull { it !in matched }
                val second = first?.let { f -> cards.indices.firstOrNull { it != f && it !in matched && cards[it] == cards[f] } }
                if (first != null && second != null) {
                    hintUsed = true
                    hintPair = setOf(first, second)
                }
            }) { Text("💡", fontSize = 34.sp) }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.height(cardHeight * rows + 8.dp * (rows - 1)),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards.size) { index ->
                val open = index in revealed || index in matched || index in hintPair
                Surface(
                    modifier = Modifier.fillMaxWidth().height(cardHeight).clickable(enabled = index !in matched && index !in revealed && !mismatch) {
                        val next = revealed + index
                        if (next.size == 2) {
                            attempts++
                            hintPair = emptySet()
                            if (cards[next[0]] == cards[next[1]]) {
                                matched = matched + next
                                revealed = emptyList()
                                if (matched.size == cards.size) onComplete(attempts, hintUsed)
                            } else {
                                revealed = next
                                mismatch = true
                                mismatchToken++
                            }
                        } else revealed = next
                    },
                    color = if (index in matched) Color(0xFFBDEDD7) else Color(0xFFD7EBFF),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (open) cards[index] else "❔", fontSize = if (rows <= 2) 60.sp else if (rows == 3) 48.sp else 40.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ShadowPlayScreen(level: Int, round: Int, onComplete: (Int, Boolean) -> Unit) {
    val context = LocalContext.current
    val levelData = remember(context, level) { com.yonamatch.app.data.ShadowLevelRepository(context).levels()[level - 1] }
    val game = remember(level, round) { com.yonamatch.app.data.ShadowGame(levelData.items) }
    var selection by remember(level, round) { mutableStateOf<String?>(null) }
    var selectedRight by remember(level, round) { mutableStateOf<String?>(null) }
    var hintUsed by remember(level, round) { mutableStateOf(false) }
    var revision by remember(level, round) { mutableIntStateOf(0) }
    val completed = game.matched.toSet()
    val shadowCardHeight = ((LocalConfiguration.current.screenHeightDp.dp - 220.dp - 8.dp * (game.items.size - 1)) / game.items.size).coerceAtLeast(78.dp)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("🐘", fontSize = 32.sp)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                selection = game.items.firstOrNull { it.id !in completed }?.id
                selectedRight = null
                if (selection != null) hintUsed = true
            }) { Text("💡", fontSize = 34.sp) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(false, true).forEach { target ->
                val entries = if (target) game.targets else game.items
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.weight(1f).height(entries.size * shadowCardHeight + 8.dp * (entries.size - 1)),
                    userScrollEnabled = false,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries, key = { it.id }) { item ->
                        val solved = item.id in completed
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(shadowCardHeight).clickable(enabled = !solved) {
                                if (target) {
                                    if (selection != null) {
                                        game.match(sourceId = selection!!, targetId = item.id)
                                        revision++
                                        selection = null
                                        selectedRight = null
                                        if (game.isComplete) onComplete(game.attempts, hintUsed)
                                    } else selectedRight = item.id
                                } else {
                                    if (selectedRight != null) {
                                        game.match(sourceId = item.id, targetId = selectedRight!!)
                                        revision++
                                        selection = null
                                        selectedRight = null
                                        if (game.isComplete) onComplete(game.attempts, hintUsed)
                                    } else selection = item.id
                                }
                            },
                            color = if (solved) Color(0xFFBDEDD7) else if ((target && selectedRight == item.id) || (!target && selection == item.id)) Color(0xFFFFE4A0)
                                else if (target) Color(0xFFECE6FF) else Color(0xFFE0F7EC),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (solved) "✓" else item.emoji, fontSize = 48.sp,
                                    color = if (target) Color.DarkGray.copy(alpha = 0.4f) else Color.Black)
                            }
                        }
                    }
                }
            }
        }
        if (revision > 0) Text("⭐ ${completed.size}/${game.items.size}", fontSize = 22.sp)
    }
}
