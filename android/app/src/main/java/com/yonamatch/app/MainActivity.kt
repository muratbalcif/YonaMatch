package com.yonamatch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val teal = Color(0xFF0F766E)
private val cream = Color(0xFFFFF9F0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { YonaMatchApp() } }
    }
}

@Composable
private fun YonaMatchApp() {
    var playing by remember { mutableStateOf(false) }
    var round by remember { mutableIntStateOf(0) }
    val symbols = remember(round) { listOf("🐱", "🐶", "🐰", "🐱", "🐶", "🐰").shuffled() }
    var opened by remember(round) { mutableStateOf<List<Int>>(emptyList()) }
    var matched by remember(round) { mutableStateOf<Set<Int>>(emptySet()) }
    var attempts by remember(round) { mutableIntStateOf(0) }
    var mismatch by remember(round) { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(cream).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("🧩 YonaMatch", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = teal)
        if (!playing) {
            Text("Eşleştir, keşfet, eğlen!", fontSize = 19.sp)
            Button(onClick = { playing = true }, colors = ButtonDefaults.buttonColors(containerColor = teal)) {
                Text("Hafıza Kartlarını Oyna")
            }
            listOf("Gölgesini Bul", "Şekil Eşleştirme", "Renk Eşleştirme", "Parçaları Birleştir", "İlişki Kurma").forEach {
                Text("🔒 $it — Yakında", color = Color.DarkGray)
            }
        } else {
            Text("Bölüm 1 · Deneme: $attempts · Çift: ${matched.size / 2}/3")
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(260.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(symbols) { index, symbol ->
                    val visible = index in opened || index in matched
                    Box(
                        Modifier.aspectRatio(1f).background(if (index in matched) Color(0xFFB9E9D4) else Color(0xFFFFDBA6), RoundedCornerShape(16.dp))
                            .clickable(enabled = index !in matched && index !in opened && !mismatch) {
                                val next = opened + index
                                if (next.size == 2) {
                                    attempts++
                                    if (symbols[next[0]] == symbols[next[1]]) {
                                        matched = matched + next
                                        opened = emptyList()
                                    } else { opened = next; mismatch = true }
                                } else opened = next
                            },
                        contentAlignment = Alignment.Center
                    ) { Text(if (visible) symbol else "❔", fontSize = 36.sp) }
                }
            }
            if (mismatch) Button(onClick = { opened = emptyList(); mismatch = false }) { Text("Tekrar dene") }
            if (matched.size == symbols.size) {
                Text("🎉 Harika! Hepsini buldun!", color = teal, fontWeight = FontWeight.Bold)
                Button(onClick = { round++ }) { Text("Yeniden Oyna") }
            }
            TextButton(onClick = { playing = false; round++ }) { Text("Ana Sayfa") }
        }
    }
}
