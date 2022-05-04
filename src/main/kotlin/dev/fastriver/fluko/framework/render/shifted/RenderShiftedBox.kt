package dev.fastriver.fluko.framework.render.shifted

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.render.BoxParentData
import dev.fastriver.fluko.framework.render.RenderBox
import dev.fastriver.fluko.framework.render.RenderObjectVisitor
import dev.fastriver.fluko.framework.render.RenderObjectWithChild

abstract class RenderShiftedBox: RenderBox(), RenderObjectWithChild<RenderBox> {
    override var child: RenderBox? by RenderObjectWithChild.ChildDelegate()

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.let {
            val childParentData = it.parentData as BoxParentData
            context.paintChild(it, childParentData.offset + offset)
        }
    }

    override fun hitTestChildren(result: HitTestResult, position: Offset): Boolean {
        child?.let {
            val childParentData = it.parentData as BoxParentData
            return result.addWithPaintOffset(
                offset = childParentData.offset,
                position = position,
                hitTest = { hitTestResult, transformed ->
                    return@addWithPaintOffset it.hitTest(hitTestResult, transformed)
                }
            )
        }
        return false
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChild(owner)
    }

    override fun detach() {
        super.detach()
        detachChild()
    }

    override fun visitChildren(visitor: RenderObjectVisitor) {
        super<RenderObjectWithChild>.visitChildren(visitor)
    }

    override fun redepthChildren() {
        super<RenderObjectWithChild>.redepthChildren { redepthChild(it) }
    }
}