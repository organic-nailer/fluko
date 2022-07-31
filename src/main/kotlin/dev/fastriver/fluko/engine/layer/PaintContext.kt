package dev.fastriver.fluko.engine.layer

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.DirectContext

data class PaintContext(
    val canvas: Canvas, val context: DirectContext
)