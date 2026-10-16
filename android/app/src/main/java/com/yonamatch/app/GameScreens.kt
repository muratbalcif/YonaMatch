package com.yonamatch.app

import androidx.compose.runtime.Composable

/** Separate screen entry points; navigation selects one screen, not one shared page. */
@Composable fun MemoryGameScreen(content: @Composable () -> Unit) = content()
@Composable fun ShadowGameScreen(content: @Composable () -> Unit) = content()
@Composable fun ShapeGameScreen(content: @Composable () -> Unit) = content()
@Composable fun ColorGameScreen(content: @Composable () -> Unit) = content()
@Composable fun HalvesGameScreen(content: @Composable () -> Unit) = content()
@Composable fun AssociationGameScreen(content: @Composable () -> Unit) = content()
