package com.yonamatch.app

import android.os.Bundle
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
private fun HalfVisual(item: MatchItem, left: Boolean) {
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
    var page by remember { mutableStateOf("home") }
    var selected by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var round by remember { mutableIntStateOf(0) }
    val cards = remember(round, level) {
        val count = levels[level - 1].pairs
        (listOf("🦁", "🐘", "🐰", "🐼", "🐧", "🐱", "🐶", "🐻").take(count).let { it + it }).shuffled()
    }
    var opened by remember(round, level) { mutableStateOf<List<Int>>(emptyList()) }
    var matched by remember(round, level) { mutableStateOf<Set<Int>>(emptySet()) }
    var attempts by remember(round, level) { mutableIntStateOf(0) }
    var mismatch by remember(round, level) { mutableStateOf(false) }
    var unlocked by remember { mutableIntStateOf(progress.unlocked()) }
    val shadowPrefs = remember(context) { context.getSharedPreferences("yonamatch_shadow", 0) }
    var shadowUnlocked by remember { mutableIntStateOf(shadowPrefs.getInt("unlocked", 1)) }
    var matchRound by remember { mutableIntStateOf(0) }
    val matchLevels = remember(selected, context) { MatchLevelRepository(context).levels(when (selected) { 2 -> "shape"; 3 -> "color"; else -> "halves" }) }
    val matchItems = remember(selected, level, matchRound) { matchLevels[level - 1].items }
    val matchGame = remember(selected, level, matchRound) { MatchGame(matchItems) }
    var matchSelection by remember(selected, level, matchRound) { mutableStateOf<String?>(null) }
    var matchCount by remember(selected, level, matchRound) { mutableIntStateOf(0) }
    val matchPrefs = remember(context) { context.getSharedPreferences("yonamatch_matching", 0) }
    var shapeUnlocked by remember { mutableIntStateOf(matchPrefs.getInt("shape_unlocked", 1)) }
    var colorUnlocked by remember { mutableIntStateOf(matchPrefs.getInt("color_unlocked", 1)) }
    var halvesUnlocked by remember { mutableIntStateOf(matchPrefs.getInt("halves_unlocked", 1)) }
    var matchFeedback by remember(selected, level, matchRound) { mutableStateOf("Kartı ve hedefini eşleştir.") }
    var shadowRound by remember { mutableIntStateOf(0) }
    val shadowLevels = remember(context) { ShadowLevelRepository(context).levels() }
    val shadow = remember(shadowRound, level) { ShadowGame(shadowLevels[level - 1].items) }
    var selectedShadow by remember(shadowRound) { mutableStateOf<String?>(null) }
    var shadowMatches by remember(shadowRound) { mutableIntStateOf(0) }
    var shadowFeedback by remember(shadowRound) { mutableStateOf("Önce hayvanı, sonra gölgesini seç.") }
    LaunchedEffect(mismatch) {
        if (mismatch) {
            kotlinx.coroutines.delay(900)
            opened = emptyList()
            mismatch = false
        }
    }
    LaunchedEffect(matched.size) {
        if (page == "game" && cards.isNotEmpty() && matched.size == cards.size) {
            val next = (level + 1).coerceAtMost(20)
            if (next > unlocked) {
                unlocked = next
            }
            progress.finish(level, if (attempts <= levels[level - 1].idealAttempts) 3 else 2)
            page = "result"
        }
    }
    Column(
        Modifier.fillMaxSize().background(cream).verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (page != "home") TextButton(onClick = { page = when(page) { "game", "result" -> "levels"; else -> "home" } }) { Text(if (page == "game" && selected in 2..4) "‹" else "‹ Geri", color = navy, fontSize = if (page == "game" && selected in 2..4) 30.sp else 16.sp) }
            Spacer(Modifier.weight(1f))
            if (page != "game" || selected !in 2..4) Text("🧩 YonaMatch", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = navy)
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
                if (selected > 4) {
                    Text("Bu oyun yakında hazır olacak. Şimdilik Hafıza Kartları'nı oynayabilirsin.", color = navy)
                    Button(onClick = { selected = 0 }) { Text("Hafıza Kartları") }
                } else {
                    Text("Bir bölüm seç", color = navy)
                    LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(450.dp), userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(20) { index ->
                            val n = index + 1
                            val isUnlocked = n <= if (selected == 1) shadowUnlocked else unlocked

                            Surface(
                                modifier = Modifier.aspectRatio(1f).clickable(enabled = n <= (when (selected) { 1 -> shadowUnlocked; 2 -> shapeUnlocked; 3 -> colorUnlocked; 4 -> halvesUnlocked; else -> unlocked })) { level = n; when (selected) { 1 -> shadowRound++; 2, 3, 4 -> matchRound++; else -> round++ }; page = "game" },
                                color = if (n <= (when (selected) { 1 -> shadowUnlocked; 2 -> shapeUnlocked; 3 -> colorUnlocked; else -> unlocked })) yellow.copy(alpha = 0.35f) else Color.LightGray.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) { Text(if (n <= (when (selected) { 1 -> shadowUnlocked; 2 -> shapeUnlocked; 3 -> colorUnlocked; else -> unlocked })) "$n ⭐" else "🔒", fontSize = 18.sp) }
                            }
                        }
                    }
                }
            }
            "game" -> {
                if (selected in 2..4) {
                    Text(when (selected) { 2 -> "🔺"; 3 -> "🎨"; else -> "🧩" }, fontSize = 30.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        matchGame.items.forEach { item ->
                            if (item.id !in matchGame.matched) {
                                Surface(Modifier.weight(1f).height(84.dp).clickable { matchSelection = item.id }
                                    .dragAndDropSource { startTransfer(DragAndDropTransferData(ClipData.newPlainText("match-id", item.id))) },
                                    color = if (matchSelection == item.id) yellow else mint.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(14.dp)) {
                                    Box(contentAlignment = Alignment.Center) { if (selected == 4) HalfVisual(item, left = true) else MatchVisual(item, selected == 3) }
                                }
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        matchGame.targets.forEach { item ->
                            val completeMatch: (String) -> Unit = { source ->
                                val correct = matchGame.match(source, item.id)
                                matchCount = matchGame.matched.size
                                matchFeedback = if (correct) "Harika!" else "Tekrar deneyelim!"
                                matchSelection = null
                                if (matchGame.complete) {
                                    val next = (level + 1).coerceAtMost(20)
                                    when (selected) { 2 -> shapeUnlocked = maxOf(shapeUnlocked, next); 3 -> colorUnlocked = maxOf(colorUnlocked, next); 4 -> halvesUnlocked = maxOf(halvesUnlocked, next) }
                                    matchPrefs.edit().putInt(when (selected) { 2 -> "shape_unlocked"; 3 -> "color_unlocked"; else -> "halves_unlocked" }, when (selected) { 2 -> shapeUnlocked; 3 -> colorUnlocked; else -> halvesUnlocked }).apply()
                                    page = "result"
                                }
                            }
                            Surface(Modifier.weight(1f).height(84.dp)
                                .dragAndDropTarget(shouldStartDragAndDrop = { true }, target = remember(item.id, level, matchRound) {
                                    object : DragAndDropTarget {
                                        override fun onDrop(event: DragAndDropEvent): Boolean {
                                            val source = event.toAndroidDragEvent().clipData?.getItemAt(0)?.text?.toString() ?: return false
                                            completeMatch(source)
                                            return true
                                        }
                                    }
                                })
                                .clickable(enabled = matchSelection != null && item.id !in matchGame.matched) { completeMatch(matchSelection!!) },
                                color = if (item.id in matchGame.matched) mint.copy(alpha = 0.5f) else lavender.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(14.dp)) {
                                Box(contentAlignment = Alignment.Center) { if (item.id in matchGame.matched) Text("✓", fontSize = 32.sp) else if (selected == 4) HalfVisual(item, left = false) else MatchVisual(item, selected == 3, faded = selected == 2) }
                            }
                        }
                    }
                    if (selected == 4) {
                        AnimatedVisibility(visible = matchCount > 0) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("✨", fontSize = 28.sp)
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.height(170.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(matchGame.items.filter { it.id in matchGame.matched }, key = { it.id }) { solved ->
                                        Surface(color = mint.copy(alpha = 0.3f), shape = RoundedCornerShape(18.dp)) {
                                            Box(Modifier.height(72.dp), contentAlignment = Alignment.Center) {
                                                JoiningVisual(solved)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    TextButton(onClick = { matchRound++ }) { Text("↻", fontSize = 30.sp) }
                } else if (selected == 1) {
                    Text("🐘 Gölgesini Bul", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(shadowFeedback)
                    Text("Hayvanlar", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        shadow.items.forEach { item ->
                            if (item.id !in shadow.matched) {
                                Surface(
                                    Modifier.weight(1f).height(90.dp).clickable { selectedShadow = item.id }.dragAndDropSource { startTransfer(DragAndDropTransferData(ClipData.newPlainText("shadow-id", item.id))) },
                                    color = if (selectedShadow == item.id) yellow else blue.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(16.dp)
                                ) { Box(contentAlignment = Alignment.Center) { Text(item.emoji, fontSize = 40.sp) } }
                            }
                        }
                    }
                    Text("Gölgesini seç", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        shadow.targets.forEach { item ->
                            Surface(
                                Modifier.weight(1f).height(90.dp).dragAndDropTarget(shouldStartDragAndDrop = { true }, target = remember(item.id, shadowRound, level) { object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        val clip = event.toAndroidDragEvent().clipData
                                        val source = clip?.getItemAt(0)?.text?.toString() ?: return false
                                        val success = shadow.match(source, item.id)
                                        shadowMatches = shadow.matched.size
                                        shadowFeedback = if (success) "Harika, doğru eşleşme!" else "Tekrar deneyelim!"
                                        selectedShadow = null
                                        if (shadow.isComplete) { shadowUnlocked = maxOf(shadowUnlocked, (level + 1).coerceAtMost(20)); shadowPrefs.edit().putInt("unlocked", shadowUnlocked).apply(); page = "result" }
                                        return true
                                    }
                                } }).clickable(enabled = selectedShadow != null && item.id !in shadow.matched) {
                                    val success = shadow.match(selectedShadow!!, item.id)
                                    shadowMatches = shadow.matched.size
                                    shadowFeedback = if (success) "Harika, doğru eşleşme!" else "Tekrar deneyelim!"
                                    selectedShadow = null
                                    if (shadow.isComplete) { shadowUnlocked = maxOf(shadowUnlocked, (level + 1).coerceAtMost(20)); shadowPrefs.edit().putInt("unlocked", shadowUnlocked).apply(); page = "result" }
                                },
                                color = if (item.id in shadow.matched) mint.copy(alpha = 0.4f) else lavender.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(16.dp)
                            ) { Box(contentAlignment = Alignment.Center) { if (item.id in shadow.matched) Text("✓", fontSize = 40.sp) else Image(painter = painterResource(id = when (item.id) { "lion" -> R.drawable.shadow_lion; "elephant" -> R.drawable.shadow_elephant; "rabbit" -> R.drawable.shadow_rabbit; "cat" -> R.drawable.shadow_cat; "dog" -> R.drawable.shadow_dog; "bear" -> R.drawable.shadow_bear; "penguin" -> R.drawable.shadow_penguin; else -> R.drawable.shadow_panda }), contentDescription = "Gölge hedefi", modifier = Modifier.size(65.dp), alpha = 0.75f) } }
                        }
                    }
                    Text("Eşleşen: $shadowMatches/${shadow.items.size} · Deneme: ${shadow.attempts}")
                    TextButton(onClick = { shadowRound++ }) { Text("Yeniden Başlat") }
                } else {
                Text("Hafıza Kartları · Bölüm $level", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Çiftleri bul!  ·  Deneme: $attempts  ·  ${matched.size / 2}/${cards.size / 2}")
                val rows = (cards.size + 3) / 4
                LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height((rows * 88 + (rows - 1) * 8).dp),
                    userScrollEnabled = false, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(cards) { index, symbol ->
                        val visible = index in opened || index in matched
                        Surface(
                            modifier = Modifier.aspectRatio(1f).clickable(enabled = index !in matched && index !in opened && !mismatch) {
                                val next = opened + index
                                if (next.size == 2) {
                                    attempts++
                                    if (cards[next[0]] == cards[next[1]]) {
                                        matched = matched + next
                                        opened = emptyList()
                                    } else { opened = next; mismatch = true }
                                } else opened = next
                            },
                            color = if (index in matched) mint.copy(alpha = 0.4f) else blue.copy(alpha = 0.28f),
                            shape = RoundedCornerShape(14.dp)
                        ) { Box(contentAlignment = Alignment.Center) { AnimatedContent(targetState = visible, label = "card-flip") { isOpen -> Text(if (isOpen) symbol else "❔", fontSize = 29.sp) } } }
                    }
                }
                TextButton(onClick = { round++ }) { Text("Yeniden Başlat") }
                }
            }
            "result" -> {
                Spacer(Modifier.height(30.dp))
                Text("🎉", fontSize = 78.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("Harika! Bölümü tamamladın!", fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("⭐".repeat(if (selected in 1..4) 3 else if (attempts <= levels[level - 1].idealAttempts) 3 else 2), fontSize = 36.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                if (selected == 0) Text("Deneme sayısı: $attempts", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Button(onClick = { if (selected == 1) { level = (level + 1).coerceAtMost(20); shadowRound++ } else if (selected in 2..4) { level = (level + 1).coerceAtMost(20); matchRound++ } else { level = (level + 1).coerceAtMost(20); round++ }; page = "game" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = mint)) { Text("Sonraki Bölüm", color = navy) }
                OutlinedButton(onClick = { page = "levels" }, modifier = Modifier.fillMaxWidth()) { Text("Seviye Haritası") }
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
                    if (index != 0) Text("Yakında", color = navy, fontSize = 11.sp)
                }
            }
        }
    }
}
