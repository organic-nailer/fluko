package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.ContainerLayer
import dev.fastriver.fluko.common.layer.TransformLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.geometrics.BoxConstraints

class RenderView(width: Double, height: Double) : RenderObject(), RenderObjectWithChild<RenderBox> {
    override var size: Size = Size(width, height)
    override var child: RenderBox? by RenderObjectWithChild.ChildDelegate()
    override val isRepaintBoundary: Boolean = true
    override fun performLayout() {
        child?.layout(BoxConstraints.tight(size))
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(child != null) {
            context.paintChild(child!!, offset)
        }
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChild(owner)
    }

    override fun visitChildren(visitor: RenderObjectVisitor) {
        super<RenderObjectWithChild>.visitChildren(visitor)
    }

    override fun redepthChildren() {
        super<RenderObjectWithChild>.redepthChildren { redepthChild(it) }
    }

    fun prepareInitialFrame() {
        scheduleInitialLayout()
        scheduleInitialPaint(TransformLayer(offset = Offset.zero))
    }

    private fun scheduleInitialLayout() {
        relayoutBoundary = this
        owner!!.nodesNeedingLayout.add(this)
    }

    private fun scheduleInitialPaint(rootLayer: ContainerLayer) {
        layer = rootLayer
        owner!!.nodesNeedingPaint.add(this)
    }
}