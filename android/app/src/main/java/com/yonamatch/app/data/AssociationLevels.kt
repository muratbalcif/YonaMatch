package com.yonamatch.app.data

import android.content.Context
import org.json.JSONObject

data class AssociationPair(val source: MatchItem, val target: MatchItem)
data class AssociationLevel(val order: Int, val pairs: List<AssociationPair>, val idealAttempts: Int)

class AssociationLevelRepository(private val context: Context) {
    fun levels(): List<AssociationLevel> {
        val root = JSONObject(context.assets.open("levels/association.json").bufferedReader().use { it.readText() })
        require(root.getInt("schemaVersion") == 1 && root.getString("gameType") == "association")
        val catalogArray = root.getJSONArray("catalog")
        val catalog = (0 until catalogArray.length()).associate { index ->
            val item = catalogArray.getJSONObject(index)
            item.getString("id") to MatchItem(item.getString("id"), item.getString("symbol"), item.getString("label"))
        }
        val entries = root.getJSONArray("levels")
        return (0 until entries.length()).map { index ->
            val entry = entries.getJSONObject(index)
            val pairEntries = entry.getJSONArray("pairs")
            val pairs = (0 until pairEntries.length()).map { pairIndex ->
                val pair = pairEntries.getJSONObject(pairIndex)
                AssociationPair(
                    catalog.getValue(pair.getString("source")),
                    catalog.getValue(pair.getString("target"))
                )
            }
            require(pairs.map { it.source.id }.distinct().size == pairs.size)
            require(pairs.map { it.target.id }.distinct().size == pairs.size)
            AssociationLevel(entry.getInt("order"), pairs, entry.getInt("idealAttempts"))
        }.also { require(it.size == 20 && it.map(AssociationLevel::order) == (1..20).toList()) }
    }
}
