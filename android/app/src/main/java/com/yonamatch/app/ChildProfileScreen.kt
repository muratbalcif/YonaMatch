package com.yonamatch.app

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChildProfileScreen(onContinue: (String, String, String) -> Unit) {
    val avatars = listOf("🦁", "🐰", "🐼", "🐘")
    var avatar by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("Yona") }
    var age by remember { mutableStateOf("3-4") }
    Column(Modifier.fillMaxSize().background(Color(0xFFFFF9F0))
        .verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("Kendi Profilini Oluştur", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = { avatar = (avatar + 3) % 4 }) { Text("❮", fontSize = 28.sp) }
            Surface(Modifier.size(156.dp), shape = RoundedCornerShape(100.dp),
                color = Color(0xFFFFD85A)) {
                Box(contentAlignment = Alignment.Center) { Text(avatars[avatar], fontSize = 96.sp) }
            }
            TextButton(onClick = { avatar = (avatar + 1) % 4 }) { Text("❯", fontSize = 28.sp) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            avatars.indices.filter { it != avatar }.take(3).forEach { i ->
                Surface(Modifier.size(72.dp).clickable { avatar = i },
                    shape = RoundedCornerShape(100.dp), color = Color(0xFFBFE9FF)) {
                    Box(contentAlignment = Alignment.Center) { Text(avatars[i], fontSize = 46.sp) }
                }
            }
        }
        OutlinedTextField(name, { name = it.take(24) }, label = { Text("Adın") },
            singleLine = true, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth())
        Text("Yaş Grubu", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("3-4", "5-6", "7-8").forEach { option ->
                Surface(Modifier.weight(1f).height(54.dp).clickable { age = option },
                    shape = RoundedCornerShape(30.dp),
                    color = if (age == option) Color(0xFF35C391) else Color(0xFFF2ECE6)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(option, fontSize = 20.sp,
                            color = if (age == option) Color.White else Color(0xFF183454))
                    }
                }
            }
        }
        Button(onClick = { onContinue(name.trim(), avatars[avatar], age) },
            enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth().height(66.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCA39))) {
            Text("Devam", fontSize = 22.sp, color = Color(0xFF183454))
        }
    }
}
