package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.ContainerLayer
import dev.fastriver.fluko.common.layer.TransformLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.geometrics.BoxConstraints

class RenderView(width: Double, height: Double) : RenderObject(), RenderObjectWithChildMixin<RenderBox> {
    override var size: Size = Size(width, height)
    override var child: RenderBox? = null
    val layer: ContainerLayer = TransformLayer()
    override fun layout(constraints: BoxConstraints) {
        throw NotImplementedError()
    }

    fun performLayout() {
        child?.layout(BoxConstraints.tight(size))
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(child != null) {
            child!!.paint(context, offset)
        }
    }
}