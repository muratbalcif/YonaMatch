package com.yonamatch.app.data

data class MatchItem(val id: String, val symbol: String, val label: String)
class MatchGame(val items: List<MatchItem>, val associationTargets: Map<String, MatchItem> = emptyMap()) {
    init { require(items.isNotEmpty() && items.map { it.id }.distinct().size == items.size) }
    val targets = (if (associationTargets.isEmpty()) items else items.map { associationTargets.getValue(it.id) }).shuffled()
    val matched = mutableSetOf<String>()
    var attempts = 0
        private set
    fun match(source: String, target: String): Boolean {
        if (source !in items.map { it.id } || target !in targets.map { it.id }) return false
        if (source in matched || target in matched) return false
        attempts++
        if ((associationTargets[source]?.id ?: source) == target) { matched.add(source); return true }
        return false
    }
    val complete get() = matched.size == items.size
}
