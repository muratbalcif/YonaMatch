package com.yonamatch.app.data

import android.content.Context
import org.json.JSONObject

data class ShadowLevel(val id: String, val order: Int, val items: List<ShadowItem>, val idealAttempts: Int)

class ShadowLevelRepository(private val context: Context) {
    fun levels(): List<ShadowLevel> {
        val raw = context.assets.open("levels/shadow.json").bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        require(root.getInt("schemaVersion") == 1 && root.getString("gameType") == "shadow")
        val catalogArray = root.getJSONArray("catalog")
        val catalog = (0 until catalogArray.length()).associate { i ->
            val item = catalogArray.getJSONObject(i)
            val id = item.getString("id")
            id to ShadowItem(id, item.getString("emoji"), item.getString("label"))
        }
        val levels = root.getJSONArray("levels")
        return (0 until levels.length()).map { i ->
            val item = levels.getJSONObject(i)
            val ids = item.getJSONArray("items")
            val animals = (0 until ids.length()).map { j ->
                catalog[ids.getString(j)] ?: error("Unknown shadow item")
            }
            require(animals.map { it.id }.distinct().size == animals.size)
            ShadowLevel(item.getString("id"), item.getInt("order"), animals, item.getInt("idealAttempts"))
        }.also { require(it.size == 20 && it.map { level -> level.order } == (1..20).toList()) }
    }
}
