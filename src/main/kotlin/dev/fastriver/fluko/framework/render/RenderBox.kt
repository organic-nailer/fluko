package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.PointerEvent
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.gesture.HitTestEntry
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.gesture.HitTestTarget

abstract class RenderBox : RenderObject() {
    override var size: Size = Size.zero

    open fun hitTest(result: HitTestResult, position: Offset): Boolean {
        if(size.contains(position)) {
            if(hitTestChildren(result, position) || hitTestSelf(position)) {
                result.add(HitTestEntry(this))
                return true
            }
        }
        return false
    }

    open fun hitTestChildren(result: HitTestResult, position: Offset): Boolean = false

    open fun hitTestSelf(position: Offset): Boolean = false

    override fun handleEvent(event: PointerEvent, entry: HitTestEntry) {
        // Do Nothing
    }
}