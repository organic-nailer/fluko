package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.PointerEvent
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.math.Matrix4
import dev.fastriver.fluko.framework.gesture.HitTestEntry
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.gesture.HitTestTarget
import org.jetbrains.skia.Rect

abstract class RenderBox : RenderObject() {
    override var size: Size = Size.zero

    override fun setupParentData(child: RenderObject) {
        child.parentData = BoxParentData()
    }

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

    override fun applyPaintTransform(child: RenderObject, transform: Matrix4): Matrix4 {
        val childParentData = child.parentData as BoxParentData
        val offset = childParentData.offset
        return transform * Matrix4.translationValues(offset.dx.toFloat(), offset.dy.toFloat(), 0f)
    }

    override val semanticBounds: Rect
        get() = size.and(Offset.zero)
}