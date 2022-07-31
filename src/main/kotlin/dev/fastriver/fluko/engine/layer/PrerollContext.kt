package dev.fastriver.fluko.engine.layer

import org.jetbrains.skia.Rect

data class PrerollContext(
    var cullRect: Rect
)