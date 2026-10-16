package com.yonamatch.app

import android.os.Bundle
import com.yonamatch.app.data.LevelRepository
import com.yonamatch.app.data.ProgressRepository
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                progress.finish(level, if (attempts <= levels[level - 1].idealAttempts) 3 else 2)
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
            if (page != "home") TextButton(onClick = { page = when(page) { "game", "result" -> "levels"; else -> "home" } }) { Text("‹ Geri", color = navy) }
            Spacer(Modifier.weight(1f))
            Text("🧩 YonaMatch", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = navy)
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
                if (selected != 0) {
                    Text("Bu oyun yakında hazır olacak. Şimdilik Hafıza Kartları'nı oynayabilirsin.", color = navy)
                    Button(onClick = { selected = 0 }) { Text("Hafıza Kartları") }
                } else {
                    Text("Bir bölüm seç", color = navy)
                    LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(450.dp), userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(20) { index ->
                            val n = index + 1
                            Surface(
                                modifier = Modifier.aspectRatio(1f).clickable(enabled = n <= unlocked) { level = n; round++; page = "game" },
                                color = if (n <= unlocked) yellow.copy(alpha = 0.35f) else Color.LightGray.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) { Text(if (n <= unlocked) "$n ⭐" else "🔒", fontSize = 18.sp) }
                            }
                        }
                    }
                }
            }
            "game" -> {
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
                        ) { Box(contentAlignment = Alignment.Center) { Text(if (visible) symbol else "❔", fontSize = 29.sp) } }
                    }
                }
                TextButton(onClick = { round++ }) { Text("Yeniden Başlat") }
            }
            "result" -> {
                Spacer(Modifier.height(30.dp))
                Text("🎉", fontSize = 78.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("Harika! Bölümü tamamladın!", fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("⭐".repeat(if (attempts <= levels[level - 1].idealAttempts) 3 else 2), fontSize = 36.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text("Deneme sayısı: $attempts", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Button(onClick = { level = (level + 1).coerceAtMost(20); round++; page = "game" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = mint)) { Text("Sonraki Bölüm", color = navy) }
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
