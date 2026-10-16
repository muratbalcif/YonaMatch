package com.yonamatch.app.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameProgressStoreTest {
    @Test fun sixGamesKeepBestStarsAfterReopen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        for (game in listOf("memory", "shadow", "shape", "color", "halves", "association")) {
            val store = GameProgressStore(context)
            assertEquals(2, store.finish(game, 1, 7, 3))
            assertEquals(3, store.finish(game, 1, 3, 3))
            assertEquals(1, store.finish(game, 1, 8, 3, hintsUsed = true))
            val reopened = GameProgressStore(context)
            assertEquals(3, reopened.bestStars(game, 1))
            assertEquals(2, reopened.unlocked(game))
            reopened.finish(game, 20, 3, 3)
            assertEquals(20, GameProgressStore(context).unlocked(game))
        }
    }

    @Test fun migratesLegacyMatchingAndShadowProgress() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("yonamatch_game_progress_v1", 0).edit().clear().commit()
        context.getSharedPreferences("yonamatch_matching", 0).edit().putInt("shape_unlocked", 9).commit()
        context.getSharedPreferences("yonamatch_shadow", 0).edit().putInt("unlocked", 7).commit()
        val store = GameProgressStore(context)
        assertEquals(9, store.unlocked("shape"))
        assertEquals(7, store.unlocked("shadow"))
        assertEquals(9, GameProgressStore(context).unlocked("shape"))
    }
}
