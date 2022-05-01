package dev.fastriver.fluko.framework.widget.paint

import dev.fastriver.fluko.framework.render.RenderOpacity
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class Opacity(
    val opacity: Double, child: Widget? = null
) : SingleChildRenderObjectWidget<RenderOpacity>(child) {
    override fun createRenderObject(): RenderOpacity {
        return RenderOpacity(opacity)
    }

    override fun updateRenderObject(renderObject: RenderOpacity) {
        renderObject.opacity = opacity
    }
}