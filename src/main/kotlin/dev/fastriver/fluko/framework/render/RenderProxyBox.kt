package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.gesture.HitTestResult

abstract class RenderProxyBox : RenderBox(), RenderObjectWithChild<RenderBox> {
    override var child: RenderBox? by RenderObjectWithChild.ChildDelegate()

    override fun performLayout() {
        if(child != null) {
            child!!.layout(constraints, parentUsesSize = true)
            size = child!!.size
        } else {
            size = constraints.smallest
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.let {
            context.paintChild(it, offset)
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
        return child?.hitTest(result, position) ?: false
    }
}
