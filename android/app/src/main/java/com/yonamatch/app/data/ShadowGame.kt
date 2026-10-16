package com.yonamatch.app.data

data class ShadowItem(val id: String, val emoji: String, val label: String)

class ShadowGame(val items: List<ShadowItem>) {
    val targets = items.shuffled()
    val matched = mutableSetOf<String>()
    var attempts = 0
        private set

    fun match(sourceId: String, targetId: String): Boolean {
        if (matched.contains(sourceId) || matched.contains(targetId)) return false
        if (items.none { it.id == sourceId } || items.none { it.id == targetId }) return false
        attempts += 1
        if (sourceId == targetId) {
            matched.add(sourceId)
            return true
        }
        return false
    }

    val isComplete: Boolean get() = matched.size == items.size
}
