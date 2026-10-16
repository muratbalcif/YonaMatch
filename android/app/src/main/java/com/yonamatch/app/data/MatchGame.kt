package com.yonamatch.app.data

data class MatchItem(val id: String, val symbol: String, val label: String)
class MatchGame(val items: List<MatchItem>) {
    init { require(items.isNotEmpty() && items.map { it.id }.distinct().size == items.size) }
    val targets = items.shuffled()
    val matched = mutableSetOf<String>()
    var attempts = 0
        private set
    fun match(source: String, target: String): Boolean {
        if (source !in items.map { it.id } || target !in items.map { it.id }) return false
        if (source in matched || target in matched) return false
        attempts++
        if (source == target) { matched.add(source); return true }
        return false
    }
    val complete get() = matched.size == items.size
}
