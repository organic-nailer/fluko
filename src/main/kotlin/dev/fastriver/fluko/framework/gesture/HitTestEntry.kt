package dev.fastriver.fluko.framework.gesture

import dev.fastriver.fluko.common.math.Matrix4

data class HitTestEntry(
    val target: HitTestTarget,
    var transform: Matrix4? = null
)