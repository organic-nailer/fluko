package dev.fastriver.fluko.framework.gesture

import dev.fastriver.fluko.common.PointerEvent

interface HitTestTarget {
    fun handleEvent(event: PointerEvent, entry: HitTestEntry)
}