package com.yonamatch.app.data

import android.content.Context
import org.json.JSONObject

data class MemoryLevel(val id: String, val order: Int, val pairs: Int, val idealAttempts: Int)
data class GameProgress(val unlocked: Int, val bestStars: Map<Int, Int>)

class LevelRepository(private val context: Context) {
    fun memoryLevels(): List<MemoryLevel> {
        val json = context.assets.open("levels/memory.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        require(root.getInt("schemaVersion") == 1 && root.getString("gameType") == "memory")
        val array = root.getJSONArray("levels")
        return (0 until array.length()).map { index ->
            val level = array.getJSONObject(index)
            MemoryLevel(level.getString("id"), level.getInt("order"), level.getInt("pairs"), level.getInt("idealAttempts"))
        }.also { require(it.size == 20 && it.map(MemoryLevel::order) == (1..20).toList()) }
    }
}

class ProgressRepository(context: Context) {
    private val prefs = context.getSharedPreferences("yonamatch_progress", Context.MODE_PRIVATE)
    fun unlocked(): Int = prefs.getInt("memory_unlocked", 1).coerceIn(1, 20)
    fun bestStars(level: Int): Int = prefs.getInt("memory_stars_$level", 0).coerceIn(0, 3)
    fun finish(level: Int, stars: Int) {
        require(level in 1..20 && stars in 1..3)
        prefs.edit()
            .putInt("memory_unlocked", maxOf(unlocked(), (level + 1).coerceAtMost(20)))
            .putInt("memory_stars_$level", maxOf(bestStars(level), stars))
            .apply()
    }
}
