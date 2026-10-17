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
    val profilePrefs = remember(context) { context.getSharedPreferences("yonamatch_child_profile", 0) }
    var profileReady by remember { mutableStateOf(profilePrefs.getBoolean("completed", false)) }
    val childName = profilePrefs.getString("name", "Yona") ?: "Yona"
    val childAvatar = profilePrefs.getString("avatar", "🦁") ?: "🦁"
    var openingStage by remember { mutableIntStateOf(if (profileReady) 2 else 0) }
    if (openingStage < 2) {
        OpeningScreen(
            splash = openingStage == 0,
            onSplashFinished = { openingStage = 1 },
            onStart = { openingStage = 2 }
        )
        return
    }
    if (!profileReady) {
        ChildProfileScreen { name, avatar, age ->
            profilePrefs.edit().putString("name", name).putString("avatar", avatar)
                .putString("age", age).putBoolean("completed", true).apply()
            profileReady = true
        }
        return
    }
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
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFFFE4B5), shape = RoundedCornerShape(50)) {
                        Text(childAvatar, modifier = Modifier.padding(9.dp), fontSize = 29.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Merhaba $childName! 👋", modifier = Modifier.weight(1f),
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = navy)
                    Text("⭐", fontSize = 25.sp)
                    Text(" ${listOf("memory", "shadow", "shape", "color", "halves", "association").sumOf { game -> (1..20).sumOf { stage -> gameProgress.bestStars(game, stage) } }}", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = navy)
                }
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable {
                        selected = 0
                        level = unlocked.coerceIn(1, 20)
                        round++
                        page = "game"
                    },
                    color = Color(0xFFC7F1E3), shape = RoundedCornerShape(24.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🦁", fontSize = 62.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Oynamaya Devam Et", fontSize = 14.sp, color = Color(0xFF267C73))
                            Text("Hafıza Kartları", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = navy)
                            Text("Bölüm $unlocked", fontSize = 15.sp, color = navy)
                            LinearProgressIndicator(
                                progress = { unlocked / 20f },
                                modifier = Modifier.fillMaxWidth().height(7.dp),
                                color = Color(0xFF2FBF83),
                                trackColor = Color.White
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Surface(color = Color(0xFFFFC844), shape = RoundedCornerShape(50),
                            modifier = Modifier.size(52.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("▶", fontSize = 25.sp, color = Color.White)
                            }
                        }
                    }
                }
                ModeGrid(onSelect = { selected = it; page = "levels" })
                HorizontalDivider(color = Color(0xFFE9E3DD))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⌂", fontSize = 32.sp, color = Color(0xFF164D99))
                        Text("Ana Sayfa", fontSize = 13.sp, color = navy)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { page = "levels" }) {
                        Text("🏆", fontSize = 30.sp)
                        Text("Bölümler", fontSize = 13.sp, color = navy)
                    }
                }
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
                val modeEmoji = modes[selected].emoji
                val completed = resultStars.coerceIn(0, 3)
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐  ✨  🌟", fontSize = 37.sp)
                            Text(if (selected == 0) "🦁" else modeEmoji, fontSize = 126.sp)
                            Text("🌈  🌿  🎉", fontSize = 37.sp)
                        }
                    }
                    Text("Harika!", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF34E45))
                    Text("Bölümü tamamladın!", fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = navy)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(3) { index ->
                            Text(if (index < completed) "⭐" else "☆", fontSize = 58.sp)
                        }
                    }
                    Surface(
                        color = Color(0xFFF6EFE7),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🎯  Deneme:", fontSize = 18.sp, color = navy)
                                Text("Tamamlandı", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = navy)
                            }
                            HorizontalDivider(color = Color.White)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("💡  İpucu:", fontSize = 18.sp, color = navy)
                                Text("⭐ $completed / 3", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = navy)
                            }
                            HorizontalDivider(color = Color.White)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🏆  Bölüm:", fontSize = 18.sp, color = navy)
                                Text("$level", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = navy)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(onClick = { page = "home" }, modifier = Modifier.weight(1f).height(68.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5E6FF))) {
                            Text("⌂", fontSize = 38.sp, color = Color(0xFF12478E))
                        }
                        Button(onClick = {
                            if (selected == 1) shadowRound++ else if (selected in 2..5) matchRound++ else round++
                            page = "game"
                        }, modifier = Modifier.weight(1f).height(68.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37ACF3))) {
                            Text("↻", fontSize = 38.sp, color = Color.White)
                        }
                        Button(onClick = {
                            if (level >= 20) page = "levels"
                            else {
                                level++
                                if (selected == 1) shadowRound++ else if (selected in 2..5) matchRound++ else round++
                                page = "game"
                            }
                        }, modifier = Modifier.weight(1f).height(68.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3CCB92))) {
                            Text("➜", fontSize = 38.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeGrid(onSelect: (Int) -> Unit) {
    val colors = listOf(
        Color(0xFFFFE8E8), Color(0xFFEDE3FF), Color(0xFFFFEDCA),
        Color(0xFFFFE1E8), Color(0xFFFFE0DA), Color(0xFFFFF0D1)
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.height(336.dp),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(modes) { index, mode ->
            Surface(
                modifier = Modifier.fillMaxWidth().height(160.dp).clickable { onSelect(index) },
                color = colors[index], shape = RoundedCornerShape(22.dp),
                shadowElevation = 2.dp
            ) {
                Column(Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly) {
                    Text(mode.emoji, fontSize = 49.sp)
                    Text(mode.title, fontWeight = FontWeight.SemiBold, color = Color(0xFF193A66),
                        textAlign = TextAlign.Center, fontSize = 12.sp, lineHeight = 15.sp)
                }
            }
        }
    }
}
