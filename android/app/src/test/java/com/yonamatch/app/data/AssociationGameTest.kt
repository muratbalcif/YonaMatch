package com.yonamatch.app.data

import org.junit.Assert.*
import org.junit.Test

class AssociationGameTest {
    @Test fun semanticPairsMatchActualTargetNotSource() {
        val bee = MatchItem("bee", "🐝", "Arı")
        val cow = MatchItem("cow", "🐮", "İnek")
        val honey = MatchItem("honey", "🍯", "Bal")
        val milk = MatchItem("milk", "🥛", "Süt")
        val game = MatchGame(listOf(bee, cow), mapOf("bee" to honey, "cow" to milk))
        assertEquals(setOf("honey", "milk"), game.targets.map { it.id }.toSet())
        assertFalse(game.match("bee", "milk"))
        assertTrue(game.match("bee", "honey"))
        assertFalse(game.match("bee", "honey"))
        assertTrue(game.match("cow", "milk"))
        assertTrue(game.complete)
        assertEquals(3, game.attempts)
    }
}
