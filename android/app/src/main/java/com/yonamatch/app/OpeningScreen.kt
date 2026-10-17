package com.yonamatch.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val sky = Color(0xFFB7EBFF)
private val grass = Color(0xFF80D38D)
private val darkBlue = Color(0xFF164A91)

@Composable
fun OpeningScreen(splash: Boolean, onSplashFinished: () -> Unit, onStart: () -> Unit) {
    LaunchedEffect(splash) {
        if (splash) {
            delay(1400)
            onSplashFinished()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(sky, Color(0xFFF7FCFF), Color(0xFFE5F8C2), grass))
        )
    ) {
        Text("☁️", fontSize = 84.sp, modifier = Modifier.align(Alignment.TopStart).padding(top = 72.dp))
        Text("☁️", fontSize = 68.sp, modifier = Modifier.align(Alignment.TopEnd).padding(top = 120.dp))
        Text("🌿 🌼 🌱 🌸 🌿", fontSize = 32.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp))
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 46.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(36.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (splash) {
                    Text("YonaMatch", fontSize = 51.sp, fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1685D2))
                    Text("Eşleştir · Keşfet · Öğren", fontSize = 18.sp, color = darkBlue)
                } else {
                    Text("Hoş Geldin!", fontSize = 37.sp, fontWeight = FontWeight.ExtraBold,
                        color = darkBlue)
                    Spacer(Modifier.height(12.dp))
                    Text("YonaMatch ile eğlenerek keşfet,\neşleştir ve öğren!",
                        fontSize = 19.sp, lineHeight = 27.sp, color = darkBlue,
                        textAlign = TextAlign.Center)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐", fontSize = 37.sp)
                Text("🦁", fontSize = 175.sp)
                if (!splash) Text("🐧   🐰   🐘", fontSize = 61.sp)
            }
            if (splash) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(0.65f).height(12.dp),
                        color = Color(0xFF31B5E8), trackColor = Color.White)
                    Spacer(Modifier.height(14.dp))
                    Text("Yükleniyor...", color = Color.White, fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(68.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC735))) {
                    Text("Başla  ❯", fontSize = 25.sp, fontWeight = FontWeight.Bold,
                        color = darkBlue)
                }
            }
        }
    }
}
