package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.gesture.HitTestEntry
import dev.fastriver.fluko.framework.gesture.HitTestResult
import org.jetbrains.skija.Paint

class RenderColoredBox(
    color: Int
) : RenderProxyBox() {
    var color: Int by MarkPaintProperty(color)

    override fun paint(context: PaintingContext, offset: Offset) {
        if(size.width != 0.0 && size.height != 0.0) {
            context.canvas.drawRect(size.and(offset), Paint().also { it.color = color })
        }
    }

    // RenderProxyBoxWithHitTestBehavior
    override fun hitTest(result: HitTestResult, position: Offset): Boolean {
        var hitTarget = false
        if(size.contains(position)) {
            hitTarget = hitTestChildren(result, position) || hitTestSelf(position)
            if(hitTarget) {
                result.add(HitTestEntry(this))
            }
        }
        return hitTarget
    }

    override fun hitTestSelf(position: Offset): Boolean = true
}