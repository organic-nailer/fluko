package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.render.RenderStack
import dev.fastriver.fluko.framework.render.StackFit
import dev.fastriver.fluko.framework.widget.primitive.MultiChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class Stack(
    val alignment: Alignment = Alignment.topLeft,
    val fit: StackFit = StackFit.Loose,
    val clipBehavior: Clip = Clip.HardEdge,
    children: List<Widget>
): MultiChildRenderObjectWidget<RenderStack>(children) {
    override fun createRenderObject(): RenderStack {
        return RenderStack(
            alignment, fit, clipBehavior
        )
    }

    override fun updateRenderObject(renderObject: RenderStack) {
        renderObject.let {
            it.alignment = alignment
            it.fit = fit
            it.clipBehavior = clipBehavior
        }
    }
}