package com.yonamatch.app.data

import android.content.Context
import org.json.JSONObject

data class MatchLevel(val order: Int, val items: List<MatchItem>, val idealAttempts: Int)

class MatchLevelRepository(private val context: Context) {
    fun levels(gameType: String): List<MatchLevel> {
        require(gameType == "shape" || gameType == "color" || gameType == "halves")
        val raw = context.assets.open("levels/$gameType.json").bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        require(root.getInt("schemaVersion") == 1 && root.getString("gameType") == gameType)
        val entries = root.getJSONArray("catalog")
        val catalog = (0 until entries.length()).associate { i ->
            val entry = entries.getJSONObject(i)
            val id = entry.getString("id")
            id to MatchItem(id, entry.getString("symbol"), entry.getString("label"))
        }
        val array = root.getJSONArray("levels")
        return (0 until array.length()).map { i ->
            val level = array.getJSONObject(i)
            val ids = level.getJSONArray("items")
            val items = (0 until ids.length()).map { j -> catalog[ids.getString(j)] ?: error("Unknown match item") }
            require(items.map { it.id }.distinct().size == items.size)
            MatchLevel(level.getInt("order"), items, level.getInt("idealAttempts"))
        }.also { require(it.size == 20 && it.map { level -> level.order } == (1..20).toList()) }
    }
}
