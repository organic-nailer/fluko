package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.gesture.HitTestResult

class RenderPositionedBox(
    widthFactor: Double? = null, heightFactor: Double? = null, alignment: Alignment = Alignment.center
) : RenderBox(), RenderObjectWithChild<RenderBox> {
    var widthFactor: Double? by MarkLayoutProperty(widthFactor)
    var heightFactor: Double? by MarkLayoutProperty(heightFactor)
    var alignment: Alignment by MarkLayoutProperty(alignment)

    override var child: RenderBox? by RenderObjectWithChild.ChildDelegate()

    override fun setupParentData(child: RenderObject) {
        child.parentData = BoxParentData()
    }

    override fun performLayout() {
        val shrinkWrapWidth = widthFactor != null || constraints.maxWidth == Double.POSITIVE_INFINITY
        val shrinkWrapHeight = heightFactor != null || constraints.maxHeight == Double.POSITIVE_INFINITY

        if(child != null) {
            child!!.layout(constraints.loosen(), parentUsesSize = true)
            size = constraints.constrain(
                Size(
                    if(shrinkWrapWidth) child!!.size.width * (widthFactor ?: 0.0) else Double.POSITIVE_INFINITY,
                    if(shrinkWrapHeight) child!!.size.height * (heightFactor ?: 0.0) else Double.POSITIVE_INFINITY,
                )
            )
            alignChild()
        } else {
            size = constraints.constrain(
                Size(
                    if(shrinkWrapWidth) 0.0 else Double.POSITIVE_INFINITY,
                    if(shrinkWrapHeight) 0.0 else Double.POSITIVE_INFINITY
                )
            )
        }
    }

    /// alignmentに沿うように子のoffsetを決定する
    private fun alignChild() {
        val childParentData = child!!.parentData as BoxParentData
        childParentData.offset = alignment.computeOffset(size, child!!.size)
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(child != null) {
            val childParentData = child!!.parentData as BoxParentData
            context.paintChild(child!!, childParentData.offset + offset)
        }
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

    override fun hitTestChildren(result: HitTestResult, position: Offset): Boolean {
        if(child != null) {
            val childParentData = child!!.parentData as BoxParentData
            return result.addWithPaintOffset(offset = childParentData.offset,
                position = position,
                hitTest = { result, transformed ->
                    child!!.hitTest(result, transformed)
                })
        }
        return false
    }
}