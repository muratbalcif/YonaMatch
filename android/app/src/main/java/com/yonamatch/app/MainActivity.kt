package com.yonamatch.app

import android.os.Bundle
import com.yonamatch.app.data.GameProgressStore
import com.yonamatch.app.data.AssociationLevelRepository
import com.yonamatch.app.data.MatchLevelRepository
import com.yonamatch.app.data.MatchGame
import com.yonamatch.app.data.MatchItem
import com.yonamatch.app.data.ShadowLevelRepository
import com.yonamatch.app.data.ShadowGame
import com.yonamatch.app.data.ShadowItem
import com.yonamatch.app.data.LevelRepository
import com.yonamatch.app.data.ProgressRepository
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import android.content.ClipData
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.foundation.Image
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val cream = Color(0xFFFFF9F0)
private val navy = Color(0xFF1F2937)
private val blue = Color(0xFF4DADEB)
private val mint = Color(0xFF5DD3A1)
private val yellow = Color(0xFFFFC857)
private val coral = Color(0xFFFF8FA3)
private val lavender = Color(0xFFB79CFF)

private fun matchColor(id: String): Color = when (id) {
    "red" -> Color(0xFFEF4444)
    "blue" -> Color(0xFF3B82F6)
    "green" -> Color(0xFF22C55E)
    "yellow" -> Color(0xFFFACC15)
    "purple" -> Color(0xFFA855F7)
    else -> Color.Gray
}

private fun halfDrawable(id: String): Int? = when (id) {
    "apple" -> R.drawable.halves_apple
    "butterfly" -> R.drawable.halves_butterfly
    "fish" -> R.drawable.halves_fish
    "car" -> R.drawable.halves_car
    "house" -> R.drawable.halves_house
    "flower" -> R.drawable.halves_flower
    "rocket" -> R.drawable.halves_rocket
    "turtle" -> R.drawable.halves_turtle
    else -> null
}

@Composable
internal fun HalfVisual(item: MatchItem, left: Boolean) {
    Box(Modifier.size(width = 28.dp, height = 62.dp).clipToBounds(), contentAlignment = Alignment.Center) {
        Box(Modifier.offset(x = if (left) 14.dp else (-14).dp).width(56.dp).height(62.dp),
            contentAlignment = Alignment.Center) {
            val drawable = halfDrawable(item.id)
            if (drawable != null) {
                Image(painter = painterResource(drawable), contentDescription = item.label, modifier = Modifier.size(56.dp))
            } else {
                Text(item.symbol, fontSize = 47.sp)
            }
        }
    }
}

@Composable
private fun JoiningVisual(item: MatchItem) {
    var joined by remember(item.id) { mutableStateOf(false) }
    LaunchedEffect(item.id) { joined = true }
    val offset by animateFloatAsState(
        targetValue = if (joined) 0f else 14f,
        animationSpec = tween(durationMillis = 480),
        label = "join-halves"
    )
    Box(Modifier.height(72.dp).width(86.dp), contentAlignment = Alignment.Center) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.offset(x = (-offset).dp)) { HalfVisual(item, left = true) }
            Box(Modifier.offset(x = offset.dp)) { HalfVisual(item, left = false) }
        }
    }
}

@Composable
private fun MatchVisual(item: MatchItem, isColor: Boolean, faded: Boolean = false) {
    if (isColor) {
        Box(Modifier.size(42.dp).background(matchColor(item.id).copy(alpha = if (faded) 0.25f else 1f), androidx.compose.foundation.shape.CircleShape))
    } else {
        Text(item.symbol, fontSize = 38.sp, color = navy.copy(alpha = if (faded) 0.22f else 1f))
    }
}

private data class Mode(val title: String, val emoji: String, val tint: Color)
private val modes = listOf(
    Mode("Hafıza Kartları", "🦁", yellow),
    Mode("Gölgesini Bul", "🐘", blue),
    Mode("Şekil Eşleştirme", "🔺", mint),
    Mode("Renk Eşleştirme", "🎨", lavender),
    Mode("Parçaları Birleştir", "🍎", coral),
    Mode("İlişki Kurma", "🐮", yellow)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { YonaMatchApp() } }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun YonaMatchApp() {
    val context = LocalContext.current
    val levels = remember(context) { LevelRepository(context).memoryLevels() }
    val progress = remember(context) { ProgressRepository(context) }
    val gameProgress = remember(context) { GameProgressStore(context) }
    var resultStars by remember { mutableIntStateOf(3) }
    var page by remember { mutableStateOf("home") }
    var selected by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var round by remember { mutableIntStateOf(0) }
    var unlocked by remember { mutableIntStateOf(progress.unlocked()) }
    val shadowPrefs = remember(context) { context.getSharedPreferences("yonamatch_shadow", 0) }
    var shadowUnlocked by remember { mutableIntStateOf(shadowPrefs.getInt("unlocked", 1)) }
    var matchRound by remember { mutableIntStateOf(0) }
    val matchLevels = remember(selected, context) { MatchLevelRepository(context).levels(when (selected) { 2 -> "shape"; 3 -> "color"; else -> "halves" }) }
    val associationLevels = remember(context) { AssociationLevelRepository(context).levels() }
    val matchPrefs = remember(context) { context.getSharedPreferences("yonamatch_matching", 0) }
    var shapeUnlocked by remember { mutableIntStateOf(matchPrefs.getInt("shape_unlocked", 1)) }
    var colorUnlocked by remember { mutableIntStateOf(matchPrefs.getInt("color_unlocked", 1)) }
    var halvesUnlocked by remember { mutableIntStateOf(matchPrefs.getInt("halves_unlocked", 1)) }
    var associationUnlocked by remember { mutableIntStateOf(matchPrefs.getInt("association_unlocked", 1)) }
    var shadowRound by remember { mutableIntStateOf(0) }
    val shadowLevels = remember(context) { ShadowLevelRepository(context).levels() }
    Column(
        Modifier.fillMaxSize().background(cream).then(if (page == "game") Modifier else Modifier.verticalScroll(rememberScrollState())).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (page != "home") TextButton(onClick = { page = when(page) { "game", "result" -> "levels"; else -> "home" } }) { Text(if (page == "game" && selected in 2..5) "‹" else "‹ Geri", color = navy, fontSize = if (page == "game" && selected in 2..4) 30.sp else 16.sp) }
            Spacer(Modifier.weight(1f))
            if (page != "game" || selected !in 2..5) Text("🧩 YonaMatch", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = navy)
            Spacer(Modifier.weight(1f))
        }
        when (page) {
            "home" -> {
                Text("Merhaba, minik kaşif! 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = navy)
                Text("Eşleştir, keşfet, eğlen!", color = navy)
                Surface(color = mint.copy(alpha = 0.22f), shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("🦁  Maceraya devam et!", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Text("Hafıza Kartları · Bölüm $unlocked")
                        Button(onClick = { selected = 0; page = "levels" }, colors = ButtonDefaults.buttonColors(containerColor = mint)) { Text("Devam Et", color = navy) }
                    }
                }
                Text("Oyunlarımız", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                ModeGrid(onSelect = { selected = it; page = "levels" })
            }
            "levels" -> {
                Text(modes[selected].emoji + " " + modes[selected].title, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                if (selected > 5) {
                    Text("Bu oyun yakında hazır olacak. Şimdilik Hafıza Kartları'nı oynayabilirsin.", color = navy)
                    Button(onClick = { selected = 0 }) { Text("Hafıza Kartları") }
                } else {
                    Text("Bir bölüm seç", color = navy)
                    LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(450.dp), userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(20) { index ->
                            val n = index + 1
                            val gameKey = listOf("memory", "shadow", "shape", "color", "halves", "association")[selected]
                            val bestStars = gameProgress.bestStars(gameKey, n)
                            val isUnlocked = n <= if (selected == 1) shadowUnlocked else unlocked

                            Surface(
                                modifier = Modifier.aspectRatio(1f).clickable(enabled = n <= (when (selected) { 1 -> shadowUnlocked; 2 -> shapeUnlocked; 3 -> colorUnlocked; 4 -> halvesUnlocked; 5 -> associationUnlocked; else -> unlocked })) { level = n; when (selected) { 1 -> shadowRound++; 2, 3, 4, 5 -> matchRound++; else -> round++ }; page = "game" },
                                color = if (n <= (when (selected) { 1 -> shadowUnlocked; 2 -> shapeUnlocked; 3 -> colorUnlocked; 4 -> halvesUnlocked; 5 -> associationUnlocked; else -> unlocked })) yellow.copy(alpha = 0.35f) else Color.LightGray.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) { Text(if (n <= (when (selected) { 1 -> shadowUnlocked; 2 -> shapeUnlocked; 3 -> colorUnlocked; 4 -> halvesUnlocked; 5 -> associationUnlocked; else -> unlocked })) "$n ⭐" else "🔒", fontSize = 18.sp) }
                            }
                        }
                    }
                }
            }
            "game" -> {
                if (selected == 5) {
                    AssociationGameScreen(level = level, round = matchRound) { completedAttempts, usedHint ->
                        associationUnlocked = maxOf(associationUnlocked, (level + 1).coerceAtMost(20))
                        resultStars = gameProgress.finish(
                            "association", level, completedAttempts,
                            associationLevels[level - 1].idealAttempts, hintsUsed = usedHint
                        )
                        matchPrefs.edit().putInt("association_unlocked", associationUnlocked).apply()
                        page = "result"
                    }
                } else if (selected in 2..4) {
                    val onMatchComplete: (Int, Boolean) -> Unit = { completedAttempts, usedHint ->
                        val type = when (selected) { 2 -> "shape"; 3 -> "color"; else -> "halves" }
                        resultStars = gameProgress.finish(type, level, completedAttempts, matchLevels[level - 1].idealAttempts, hintsUsed = usedHint)
                        val next = (level + 1).coerceAtMost(20)
                        when (selected) {
                            2 -> shapeUnlocked = maxOf(shapeUnlocked, next)
                            3 -> colorUnlocked = maxOf(colorUnlocked, next)
                            4 -> halvesUnlocked = maxOf(halvesUnlocked, next)
                        }
                        matchPrefs.edit().putInt("${type}_unlocked", when (selected) {
                            2 -> shapeUnlocked; 3 -> colorUnlocked; else -> halvesUnlocked
                        }).apply()
                        page = "result"
                    }
                    when (selected) {
                        2 -> ShapeGameScreen(level, matchRound, onMatchComplete)
                        3 -> ColorGameScreen(level, matchRound, onMatchComplete)
                        else -> HalvesGameScreen(level, matchRound, onMatchComplete)
                    }
                } else if (selected == 1) {
                    ShadowPlayScreen(level, shadowRound) { completedAttempts, usedHint ->
                        shadowUnlocked = maxOf(shadowUnlocked, (level + 1).coerceAtMost(20))
                        resultStars = gameProgress.finish("shadow", level, completedAttempts, shadowLevels[level - 1].idealAttempts, hintsUsed = usedHint)
                        shadowPrefs.edit().putInt("unlocked", shadowUnlocked).apply()
                        page = "result"
                    }
                } else {
                    MemoryPlayScreen(level, round) { completedAttempts, usedHint ->
                        unlocked = maxOf(unlocked, (level + 1).coerceAtMost(20))
                        resultStars = gameProgress.finish("memory", level, completedAttempts, levels[level - 1].idealAttempts, hintsUsed = usedHint)
                        progress.finish(level, resultStars)
                        page = "result"
                    }
                }
            }
            "result" -> {
                var celebrationVisible by remember(selected, level) { mutableStateOf(false) }
                LaunchedEffect(selected, level) { celebrationVisible = true }
                val celebrationScale by animateFloatAsState(
                    targetValue = if (celebrationVisible) 1f else 0.65f,
                    animationSpec = tween(durationMillis = 480),
                    label = "celebration"
                )
                val displayedStars = resultStars
                Spacer(Modifier.height(24.dp))
                Surface(
                    color = yellow.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(vertical = 30.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("🎊", fontSize = 76.sp, modifier = Modifier.graphicsLayer {
                            scaleX = celebrationScale
                            scaleY = celebrationScale
                        })
                        Text("⭐".repeat(displayedStars), fontSize = 40.sp)
                        Text("🌈  🦁  🎈", fontSize = 42.sp)
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (selected == 1) shadowRound++
                            else if (selected in 2..5) matchRound++
                            else round++
                            page = "game"
                        },
                        modifier = Modifier.weight(1f).height(68.dp)
                    ) { Text("↻", fontSize = 32.sp) }
                    Button(
                        onClick = {
                            if (level >= 20) {
                                page = "levels"
                            } else {
                                level++
                                if (selected == 1) shadowRound++
                                else if (selected in 2..5) matchRound++
                                else round++
                                page = "game"
                            }
                        },
                        modifier = Modifier.weight(2f).height(68.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = mint)
                    ) { Text(if (level == 20) "🏠" else "▶", fontSize = 34.sp, color = navy) }
                }
                OutlinedButton(
                    onClick = { page = "levels" },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) { Text("▦", fontSize = 28.sp) }

            }
        }
    }
}

@Composable
private fun ModeGrid(onSelect: (Int) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height(480.dp), userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(modes) { index, mode ->
            Surface(
                modifier = Modifier.fillMaxWidth().height(145.dp).clickable { onSelect(index) },
                color = mode.tint.copy(alpha = 0.22f), shape = RoundedCornerShape(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(mode.emoji, fontSize = 46.sp)
                    Text(mode.title, fontWeight = FontWeight.Bold, color = navy, textAlign = TextAlign.Center, fontSize = 14.sp)
                }
            }
        }
    }
}
