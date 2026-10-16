package com.yonamatch.app.data

import android.content.Context

/** Same scoring and progression rules as the native iOS implementation. */
class GameProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("yonamatch_game_progress_v1", Context.MODE_PRIVATE)
    private val names = setOf("memory", "shadow", "shape", "color", "halves", "association")

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
