package com.yonamatch.app.data

import android.content.Context

/** Same scoring and progression rules as the native iOS implementation. */
class GameProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("yonamatch_game_progress_v1", Context.MODE_PRIVATE)
    private val names = setOf("memory", "shadow", "shape", "color", "halves", "association")

    init {
        if (!prefs.getBoolean("migration_v1_complete", false)) {
            val editor = prefs.edit()
            val legacyMatches = context.getSharedPreferences("yonamatch_matching", Context.MODE_PRIVATE)
            val legacyShadow = context.getSharedPreferences("yonamatch_shadow", Context.MODE_PRIVATE)
            for (game in names) {
                val old = when (game) {
                    "shadow" -> legacyShadow.getInt("unlocked", 1)
                    "memory" -> context.getSharedPreferences("yonamatch_progress", Context.MODE_PRIVATE).getInt("unlocked", 1)
                    else -> legacyMatches.getInt("${game}_unlocked", 1)
                }
                editor.putInt("${game}_unlocked", maxOf(prefs.getInt("${game}_unlocked", 1), old).coerceIn(1, 20))
            }
            editor.putBoolean("migration_v1_complete", true).apply()
        }
    }

    fun unlocked(game: String): Int {
        require(game in names)
        return prefs.getInt("${game}_unlocked", 1).coerceIn(1, 20)
    }

    fun bestStars(game: String, level: Int): Int {
        require(game in names && level in 1..20)
        return prefs.getInt("${game}_stars_$level", 0).coerceIn(0, 3)
    }

    fun finish(game: String, level: Int, attempts: Int, idealAttempts: Int, hintsUsed: Boolean = false): Int {
        require(game in names && level in 1..20 && attempts >= 0 && idealAttempts > 0)
        val stars = if (hintsUsed) 1 else if (attempts <= idealAttempts) 3 else 2
        prefs.edit()
            .putInt("${game}_stars_$level", maxOf(bestStars(game, level), stars))
            .putInt("${game}_unlocked", maxOf(unlocked(game), (level + 1).coerceAtMost(20)))
            .apply()
        return stars
    }
}
