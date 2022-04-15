package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import dev.fastriver.fluko.framework.render.RenderConstrainedBox

class SizedBox(
    child: Widget?, val width: Double? = null, val height: Double? = null
) : SingleChildRenderObjectWidget<RenderConstrainedBox>(child) {
    private val additionalConstraints: BoxConstraints
        get() = BoxConstraints.tightFor(width, height)

    override fun createRenderObject() = RenderConstrainedBox(
        additionalConstraints = BoxConstraints.tightFor(width, height)
    )

    override fun updateRenderObject(renderObject: RenderConstrainedBox) {
        renderObject.additionalConstraints = additionalConstraints
    }
}