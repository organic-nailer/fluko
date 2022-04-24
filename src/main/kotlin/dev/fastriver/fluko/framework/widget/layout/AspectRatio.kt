package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.framework.render.RenderAspectRatio
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class AspectRatio(
    val aspectRatio: Double,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderAspectRatio>(child) {
    override fun createRenderObject(): RenderAspectRatio {
        return RenderAspectRatio(aspectRatio)
    }

    override fun updateRenderObject(renderObject: RenderAspectRatio) {
        renderObject.aspectRatio = aspectRatio
    }
}